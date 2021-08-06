package com.lann.openpark.charge.logic;

import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.bizobj.Rule;
import com.lann.openpark.charge.bizobj.config.*;
import com.lann.openpark.charge.dict.Const;
import com.lann.openpark.charge.util.DateTimeUtil;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class ParkingAlgorithm {

    private static final Logger log = LoggerFactory.getLogger(ParkingAlgorithm.class);

    private ParkingInfo parkingInfo = null;
    private Rule rule = null;


    public ParkingAlgorithm(ParkingInfo parkingInfo, Rule rule) {
        this.parkingInfo = parkingInfo;
        this.rule = rule;
    }

    /**
     * 计费规则的计费方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 11:46
     **/
    public boolean executeCalculate() throws Exception {
        if (this.parkingInfo == null || this.rule == null) {
            throw new RuntimeException("parameter incomplete");
        }

        if (this.parkingInfo.getParkLength() == 0) {
            return true;
        }

        // 1.免费时段的判断
        if (processDriverInFreeSections()) {
            return true;
        }

        // 2.计算有效停车时间
        // 全天收费，停车时长就是有效停车时长
        // 白天夜间收费，需要计算停车时间于收费时段的重叠时长，作为有效停车时长
        // 计算出ValidParkLength
        if (caclParkLengthExcludeFreeSection() == 0) {
            return true;
        }

        // 3.根据免费时间重新计算CalcDriveIn
        if (processParkingFreeTime()) {
            return true;
        }

        // 4.根据循环计费单位，拆分成循环单位数组
        splitChargeUnit();

        // 5.遍历第一个和最后一个循环计费单位，中间取日限额
        traversalCalcChargeUnit();

        // 6.附加规则计费
        executeAttachRule();

        return true;
    }

    /**
     * 附加规则的计费方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 11:46
     **/
    private void executeAttachRule() {
        processAttachPeroid();

        processAttachMoney();
    }


    /**
     * 遍历每个循环单位，并计算
     */
    private void traversalCalcChargeUnit() {
        if (this.rule.getCalcType().equals(Const.CALC_TYPE_ALL_DAY)) {// 全天计费
            if (this.rule.getAllDayChargeConfig().getDuractionRule() != null) {// 按时长
                executeAllDayDurationCharge(this.rule.getAllDayChargeConfig().getDuractionRule());
            } else {// 按时段
                executeAllDayPeriodCharge(this.rule.getAllDayChargeConfig().getPeriodRule());
            }
        } else {// 白天黑夜计费
            ChargeRuleUnit dayConfig = this.rule.getDayChargeConfig();
            ChargeRuleUnit nightConfig = this.rule.getNightChargeConfig();
            executeDayAndNightCharge(dayConfig, nightConfig);
        }
    }

    /**
     * 白天/黑夜计费方案的计费方法入口
     * 计费方法：首末段按照白天黑夜切割计费，中间取日限额后合计
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 10:43
     **/
    private void executeDayAndNightCharge(ChargeRuleUnit dayConfig, ChargeRuleUnit nightConfig) {
        Interval[] interv = this.parkingInfo.getLoopArray();// 计费算法，按照“天的定义”，按照“天”将停车时间切分的数组
        double firstFee = 0.0D;// 第一天费用
        long needBorrowTime = 0L;// 第需要借的时间
        double totalFee = 0.0D;// 总费用

        int[] daySection = this.rule.getDayInterval();

        if (interv[0].getStart().plusHours(24).compareTo(interv[0].getEnd()) == 0) {
            firstFee = this.rule.getLimitCharge();
        } else {
            double[] r = {0.0D, 0.0D};
            // 将第一个收费区间按白天黑夜切分
            Interval[] splitFirstInterval = splitFirstIntervalWhenIntervalLengthIsOne(interv[0]);
            if (interv.length == 1 && splitFirstInterval.length > 1) {
                for (int i = 0; i < splitFirstInterval.length; i++) {// 循环上边拆分的时间段，进行费用计算
                    Interval interval = splitFirstInterval[i];
                    if (i == 0) {// 第一段进行处理，计算单位费用和需要借的时间
                        r = commonDayAndNightChargeUnit(daySection, interval, true, dayConfig, nightConfig);
                        firstFee += r[0];
                        needBorrowTime = (int) r[1];
                    } else if (i == 1 && splitFirstInterval.length == 3) {
                        firstFee += commonDayAndNightChargeUnit(daySection, interval, false, dayConfig, nightConfig)[0];
                    } else {
                        interval = resumeCalculateParkingTimeAccordingBorrowTime(needBorrowTime, interval);
                        firstFee += commonDayAndNightChargeUnit(daySection, interval, false, dayConfig, nightConfig)[0];
                    }
                }
            } else {
                r = commonDayAndNightChargeUnit(daySection, interv[0], true, dayConfig, nightConfig);
                firstFee = r[0];
                needBorrowTime = (int) r[1];
            }
        }

        // 将firstfee和日限额比较
        // log.info("firstFee: " + firstFee);
        firstFee = Math.min(this.rule.getLimitCharge(), firstFee);

        for (int i = 1; i < interv.length - 1; i++) {
            totalFee += this.rule.getLimitCharge();
        }

        double lastFee = 0.0D;
        if (interv.length > 1) {
            interv[interv.length - 1] = resumeCalculateParkingTimeAccordingBorrowTime(needBorrowTime, interv[interv.length - 1]);
            Interval[] splitFirstInterval = splitFirstIntervalWhenIntervalLengthIsOne(interv[interv.length - 1]);
            for (int i = 0; i < splitFirstInterval.length; i++) {
                lastFee = lastFee + commonDayAndNightChargeUnit(daySection, splitFirstInterval[i], false, dayConfig, nightConfig)[0];
            }
            // 将lastfee和日限额比较
            // log.info("lastFee: " + lastFee);
            lastFee = Math.min(this.rule.getLimitCharge(), lastFee);
        }

        totalFee += firstFee + lastFee;
        this.parkingInfo.setCost(totalFee);
    }

    /**
     * 根据借时间重新计算最后一个 循环计费区间 的开始和结束时间
     *
     * @param needBorrowTime
     * @param interval
     * @return
     */
    private Interval resumeCalculateParkingTimeAccordingBorrowTime(
            long needBorrowTime, Interval interval) {
        DateTime newEnd = interval.getEnd();
        if (needBorrowTime > 0L) {
            newEnd = interval.getEnd().minusMinutes((int) needBorrowTime);
        }
        DateTime startDateTime = interval.getStart();
        if (startDateTime.isAfter(newEnd)) {
            newEnd = startDateTime;
        }
        interval = new Interval(startDateTime, newEnd);
        return interval;
    }

    /**
     * 用于计算每一天的收费金额，同时具有计算借时间功能
     * Description :
     *
     * @param daySection  白天黑夜的时刻 如[8,20]
     * @param interval    一个计算循环单元
     * @param isBorrow    是否需要计算借时间
     * @param dayConfig
     * @param nightConfig
     * @return [a, b] a:表示费用，b：表示借时间
     */
    private double[] commonDayAndNightChargeUnit(int[] daySection, Interval interval,
                                                 boolean isBorrow, ChargeRuleUnit dayConfig, ChargeRuleUnit nightConfig) {
        double needBorrowTime = 0.0D;
        // 声明白天费用和夜间费用
        double dayFee = 0.0D;
        double nightFee = 0.0D;
        // 求白天、黑夜的交集时间段
        Interval dayInterval = DateTimeUtil.overlap(daySection[0], daySection[1], interval);
        Interval nightInterval = DateTimeUtil.overlap(daySection[1], daySection[0], interval);

        // 如果白天有交集，则从白天计算需要借多久,以及计算费用
        if (isBorrow) {
            if (dayInterval != null) {
                needBorrowTime = dayConfig.queryDistance2Entire(dayInterval);
                if (this.rule.getBorrowTimeConfig().isDayTimeCanBeTaken()) {
                    if (needBorrowTime > this.rule.getBorrowTimeConfig().getTimeLimit()) {
                        needBorrowTime = 0.0D;
                    }
                } else {
                    needBorrowTime = 0.0D;
                }
            } else {
                needBorrowTime = nightConfig.queryDistance2Entire(nightInterval);
                if (this.rule.getBorrowTimeConfig().isNightTimeCanBeTaken()) {
                    if (needBorrowTime > this.rule.getBorrowTimeConfig().getTimeLimit()) {
                        needBorrowTime = 0.0D;
                    }
                } else {
                    needBorrowTime = 0.0D;
                }
            }
        }

        dayFee = dayConfig.getCost(dayInterval);
        nightFee = nightConfig.getCost(nightInterval);


        // double thisDayFee = Math.min(this.rule.getLimitCharge(), dayFee + nightFee);
        return new double[]{dayFee + nightFee, needBorrowTime};
    }

    /**
     * 按时段计费
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 8:42
     **/
    private void executeAllDayPeriodCharge(PeriodChargeRule periodRule) {
        Interval[] interv = this.parkingInfo.getLoopArray();
        double firstFee = 0.0D;// 第一天费用
        double lastFee = 0.0D;// 最后一天费用
        int needBorrowTime = 0;// 需要借的时间
        double totalFee = 0.0D;// 总的费用

        // 停车时间跨天
        if (interv[0].getStart().plusHours(24).compareTo(interv[0].getEnd()) == 0) {
            firstFee = this.rule.getLimitCharge();
        } else {
            // 计算需要借的时间
            needBorrowTime = periodRule.queryDistance2Entire(interv[0].getStart(), interv[0].getEnd());
            if (this.rule.getBorrowTimeConfig().isAllDayTimeCanBeTaken()) {
                if (needBorrowTime > this.rule.getBorrowTimeConfig().getTimeLimit()) {
                    needBorrowTime = 0;
                }
            } else {
                needBorrowTime = 0;
            }
            firstFee = periodRule.getCost(interv[0].getStart(), interv[0].getEnd());
            firstFee = Math.min(firstFee, this.rule.getLimitCharge());
        }

        for (int i = 1; i < interv.length - 1; i++) {
            totalFee += this.rule.getLimitCharge();
        }


        if (interv.length > 1) {
            DateTime newEnd;
            if (needBorrowTime > 0) {
                newEnd = interv[interv.length - 1].getEnd().minusMinutes(needBorrowTime);
            } else {
                newEnd = interv[interv.length - 1].getEnd();
            }
            DateTime startDateTime = interv[interv.length - 1].getStart();

            if (startDateTime.isAfter(newEnd)) {
                newEnd = startDateTime;
            }

            lastFee = Math.min(this.rule.getLimitCharge(), periodRule.getCost(startDateTime, newEnd));
        }
        totalFee += firstFee + lastFee;
        this.parkingInfo.setCost(totalFee);
    }

    /**
     * Description : 全天按时长计费通用方法
     */
    private void executeAllDayDurationCharge(DurationChargeRule dcr) {
        // 获取拆分的计费时间数组
        Interval[] interval = this.parkingInfo.getLoopArray();
        double totalFee = 0.0D;
        // 第一天的费用（正确说是：第一个循环计费单位的费用）
        double firstDayFee = 0.0D;
        // 最后一个单位的费用
        double lastDayFee = 0.0D;
        long needBorrowTime = 0L;

        // 停车时间跨天
        if (interval[0].getStart().plusHours(24).compareTo(interval[0].getEnd()) == 0) {
            firstDayFee = this.rule.getLimitCharge();
        } else {
            // 按时长计费，第一个计费单位不足一个循环单位
            // 该单位停车总时长,返回分钟数
            long parkLength = DateTimeUtil.getMinutesBetweenInterval(interval[0]);
            // 只有第一个计费循环单位需要考虑借时间，其他不考虑
            needBorrowTime = dcr.queryDistance2Entire(parkLength);
            if (this.rule.getBorrowTimeConfig().isAllDayTimeCanBeTaken()) {
                if (needBorrowTime > this.rule.getBorrowTimeConfig().getTimeLimit()) {
                    needBorrowTime = 0L;
                }
            } else {
                needBorrowTime = 0L;
            }
            firstDayFee = Math.min(this.rule.getLimitCharge(), dcr.getCost(parkLength));
        }
        // 中间有几天，直接取日限额即可
        for (int i = 1; i < interval.length - 1; i++) {
            totalFee += this.rule.getLimitCharge();
        }
        // 最后一个循环单位需要处理借出去的时间，注意，借的太多，就不借
        if (interval.length > 1) {

            DateTime newEnd, startDateTime = interval[interval.length - 1].getStart();
            if (needBorrowTime > 0L) {
                newEnd = interval[interval.length - 1].getEnd().minusMinutes((int) needBorrowTime);
            } else {
                newEnd = interval[interval.length - 1].getEnd();
            }
            // 判断是否减的超出了最后一天的范围了,理论上只通过最后一个计费单位弥补，其他时间保持完整。
            if (startDateTime.isAfter(newEnd)) {
                newEnd = startDateTime;
            }
            interval[interval.length - 1] = new Interval(startDateTime, newEnd);

            long parkLength = DateTimeUtil.getMinutesBetweenInterval(interval[interval.length - 1]);
            lastDayFee = Math.min(this.rule.getLimitCharge(), dcr.getCost(parkLength));
        }
        totalFee += firstDayFee + lastDayFee;
        this.parkingInfo.setCost(totalFee);
    }

    /**
     * 拆分停车时间为计费时间数组
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 11:47
     **/
    private void splitChargeUnit() {
        String loopType = this.rule.getLoopConfig().getLoopType();
        if (null == loopType) {
            throw new RuntimeException("parameter incomplete [lack of loop configuration]");
        }
        DateTime bTime = this.parkingInfo.getCalcDriveIn();// 减去免费时间的驶入时间
        DateTime eTime = this.parkingInfo.getCaclDriveOut();// 等于驶离时间

        Interval[] loopArray = null;

        // 24个小时为计费循环单位
        if (Const.LOOP_TWENTY_FOUR.equals(loopType)) {
            loopArray = DateTimeUtil.createIntervalArrayByLength(bTime, eTime, 24);
        }

        // 自定义时间点为循环计费单位
        if (Const.LOOP_CUSTOM.equals(loopType)) {
            int point = this.rule.getLoopConfig().getCustomPoint();
            loopArray = DateTimeUtil.createIntervalArrayByPoint(bTime, eTime, point);
        }
        this.parkingInfo.setLoopArray(loopArray);
    }


    /**
     * 免费类型（限时免费、完全免费）
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/22 15:56
     **/
    private boolean processParkingFreeTime() {
        FreeTimeConfig config = this.rule.getFreeTimeConfig();
        if (config == null) {
            return false;
        }

        if (null == config.getFreeMode() || "".equals(config.getFreeMode())) {// 免费类型
            return false;
        }

        int needFreeTime = 0;
        if (config.getFreeMode().equals(Const.ALL_FREE)) {// 完全免费
            needFreeTime = config.getFreeTime();
        } else if (this.parkingInfo.getValidParkLength() <= config.getFreeLimitTime()) {
            needFreeTime = config.getFreeTime();
        }

        if (needFreeTime == 0) {
            return false;
        }

        if (config.isAllDayAvailable()) {
            DateTime calcBeginTime = this.parkingInfo.getCalcDriveIn().plusMinutes(needFreeTime);
            this.parkingInfo.setCalcDriveIn(calcBeginTime);
        } else {
            if (this.rule.getDayInterval() == null) {
                return false;
            }

            int[] daySection = this.rule.getDayInterval();
            Boolean inDaySection = DateTimeUtil.ifTimeBelongSection(daySection, this.parkingInfo.getCalcDriveIn());
            if (inDaySection.booleanValue() == true) {// 驶入时间，在白天时段
                DateTime sectionEndDateTime = new DateTime(this.parkingInfo.getCalcDriveIn());
                // 更新时间毫秒数并返回（其实就是获得白天时段的结束时间）
                sectionEndDateTime = sectionEndDateTime.withMillisOfDay(daySection[1] * 60 * 1000);
                if (config.isDayAvailable()) {// 白天有效
                    // 驶入时间（计算用）+免费时间
                    DateTime newCaclDriveIn = this.parkingInfo.getCalcDriveIn().plusMinutes(needFreeTime);
                    // 驶入时间（计算用）+免费时间 大于 白天时间结束时间（驶入时间+免费时间超过白天时段的结束时间）
                    if (newCaclDriveIn.compareTo(sectionEndDateTime) > 0) {
                        // 需要免费时间=计算驶入时间+免费时间-白天时段结束时间（多出的时间）
                        needFreeTime = (int) (newCaclDriveIn.getMillis() - sectionEndDateTime.getMillis()) / 60000;
                        newCaclDriveIn = sectionEndDateTime;// 驶入时间（计算用） = 白天结束时段的结束时间
                        if (!this.rule.getNightChargeConfig().isFreeSection() &&
                                config.isNightAvailable()) {// 如果夜间不免费&&夜间免费有效
                            newCaclDriveIn = newCaclDriveIn.plusMinutes(needFreeTime);// 驶入时间（计算用）再加上边多出的时间
                        }
                    }

                    this.parkingInfo.setCalcDriveIn(newCaclDriveIn);
                }
            } else {// 驶入时间不在白天时段
                // 获得驶入时间在一天的哪一分钟
                int inMinute = this.parkingInfo.getCalcDriveIn().getMinuteOfDay();
                DateTime sectionEndDateTime = new DateTime(this.parkingInfo.getCalcDriveIn());
                // 获得白天时段的开始时间
                sectionEndDateTime = sectionEndDateTime.withMillisOfDay(daySection[0] * 60 * 1000);
                // 如果驶入时间的分钟数大于白天时段的结束时间分钟数
                if (inMinute >= daySection[1] && inMinute < 1440) {
                    sectionEndDateTime = sectionEndDateTime.plusDays(1);
                }
                if (config.isNightAvailable()) {
                    // 驶入时间
                    DateTime newCaclDriveIn = this.parkingInfo.getCalcDriveIn().plusMinutes(needFreeTime);
                    // 如果驶入时间+免费时间大于白天时段的开始时间
                    if (newCaclDriveIn.compareTo(sectionEndDateTime) > 0) {
                        // 需要免费时间=计算驶入时间+免费时间-白天时段开始时间（多出的时间）
                        needFreeTime = (int) (newCaclDriveIn.getMillis() - sectionEndDateTime.getMillis()) / 60000;
                        newCaclDriveIn = sectionEndDateTime;
                        if (!this.rule.getDayChargeConfig().isFreeSection() &&
                                config.isDayAvailable()) {// 如果白天不免费&&白天免费有效
                            newCaclDriveIn = newCaclDriveIn.plusMinutes(needFreeTime);// 驶入时间（计算用）再加上边多出的时间
                        }
                    }

                    this.parkingInfo.setCalcDriveIn(newCaclDriveIn);
                }
            }
        }


        if (this.parkingInfo.getCalcDriveIn().compareTo(this.parkingInfo.getCaclDriveOut()) >= 0) {
            return true;
        }

        return false;
    }

    /**
     * 计算有效的停车时间
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 11:47
     **/
    private int caclParkLengthExcludeFreeSection() {
        int timeLength = 0;
        String caclType = this.rule.getCalcType();

        if (caclType.equals(Const.CALC_TYPE_ALL_DAY)) {// 全天计费模式
            if (!this.rule.getAllDayChargeConfig().isFreeSection()) {
                timeLength = this.parkingInfo.getParkLength();
            }
        } else {// 白天夜间计费
            boolean dayFree = this.rule.getDayChargeConfig().isFreeSection();
            boolean nightFree = this.rule.getNightChargeConfig().isFreeSection();
            if (!dayFree && !nightFree) {// 白天夜间都收费
                timeLength = this.parkingInfo.getParkLength();
            }
            if (dayFree && !nightFree) {// 白天收费，夜间免费
                int[] chargeInterval = {this.rule.getDayInterval()[1], this.rule.getDayInterval()[0]};
                timeLength = DateTimeUtil.getOverLapDurationMinutes(chargeInterval, this.parkingInfo.getParkInterval());
            }
            if (!dayFree && nightFree) {// 白天免费，夜间收费
                timeLength = DateTimeUtil.getOverLapDurationMinutes(this.rule.getDayInterval(), this.parkingInfo.getParkInterval());
            }
        }
        this.parkingInfo.setValidParkLength(timeLength);
        return timeLength;
    }

    /**
     * 免费时段判断
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/22 15:55
     **/
    public boolean processDriverInFreeSections() {
        List<LocalTime[]> list = this.rule.getFreeTimeSections();
        if (list == null) {
            return false;
        }

        // 停车时间大于24小时，免费时段无效
        long parkLength = this.parkingInfo.getParkLength();
        if (parkLength >= 1440L) {
            return false;
        }

        LocalTime in = (new DateTime(this.parkingInfo.getDriveIn())).toLocalTime();
        LocalTime out = (new DateTime(this.parkingInfo.getDriveOut())).toLocalTime();

        // 循环免费时段进行判断
        for (LocalTime[] section : list) {
            // 免费时段没有跨天
            if (section[0].compareTo((ReadablePartial) section[1]) < 0) {
                // 驶入时间小于驶离时间 && 驶入时间大于免费间段起，驶离时间小于免费段止，说明停车的这段时间正好在这个免费时段内
                if (in.compareTo((ReadablePartial) out) < 0 && section[0].compareTo((ReadablePartial) in) < 0 && section[1].compareTo((ReadablePartial) out) > 0)
                    return true;
                continue;
            } else {// 免费时段跨天
                // 1.驶入驶离没有跨天
                if (out.compareTo((ReadablePartial) in) > 0) {
                    // 只要驶入时间大于免费时段或者驶离时间小于免费时段止满足一个条件即可
                    if (section[0].compareTo((ReadablePartial) in) < 0 || section[1].compareTo((ReadablePartial) out) > 0)
                        return true;
                    continue;
                }
                // 2.免费时段跨天，驶入驶离跨天
                // 这样的话，必须满足驶入时间大于免费时段起，驶离时间小于免费时段止，同时满足，说明在免费时段内
                if (section[0].compareTo((ReadablePartial) in) < 0 && section[1].compareTo((ReadablePartial) out) > 0) {
                    return true;
                }
            }
        }


        return false;
    }


    private void processAttachMoney() {
        if (this.rule.getAttachRule() == null) {
            return;
        }
        this.parkingInfo.setCost(this.parkingInfo.getCost() + this.rule.getAttachRule().getAttachMoney());
    }


    private void processAttachPeroid() {
        if (this.rule.getAttachRule() == null) {
            return;
        }

        int validParkLength = this.rule.getAttachRule().getValidParkMinutes();
        if (this.parkingInfo.getParkLength() <= validParkLength) {
            return;
        }
        int attachMoney = 0;

        List<PeriodUnit> list = this.rule.getAttachRule().getPeriods();
        if (!list.isEmpty()) {
            Interval parkInterval = new Interval(this.parkingInfo.getDriveIn().plusMinutes(validParkLength), this.parkingInfo.getDriveOut());
            for (PeriodUnit period : list) {
                int[] section = {period.getStart(), period.getEnd()};
                int overlapMinutes = DateTimeUtil.getOverLapDurationMinutes(section, parkInterval);
                int unitCount = overlapMinutes / period.getTimeUnit();
                if (overlapMinutes % period.getTimeUnit() != 0) {
                    unitCount++;
                }
                attachMoney = (int) (attachMoney + unitCount * period.getUnitCost());
            }
        }
        this.parkingInfo.setCost(this.parkingInfo.getCost() + attachMoney);
    }

    /**
     * 将第一个循环计费单位(按照“天的定义”切分) 再细分，按照 （白天->夜间  和 夜间 -> 白天） 两个分界点 拆分.
     *
     * @return
     */
//    private Interval[] splitFirstIntervalWhenIntervalLengthIsOne(Interval firstInterval) {
//        int[] daySection = this.rule.getDayInterval();
//        DateTime parkIn = new DateTime(firstInterval.getStart());
//        DateTime parkOut = new DateTime(firstInterval.getEnd());
//
//        List<Interval> list = new ArrayList<>();
//
//        int year = firstInterval.getStart().getYear();
//        int month = firstInterval.getStart().getMonthOfYear();
//        int day = firstInterval.getStart().getDayOfMonth();
//        int startHour = daySection[0] / 60;
//        int startMinute = daySection[0] % 60;
//
//        int endHour = daySection[1] / 60;
//        int endMinute = daySection[1] % 60;
//
//        DateTime nightToDay = new DateTime(year, month, day, startHour, startMinute, 0);
//        DateTime dayToNight = new DateTime(year, month, day, endHour, endMinute, 0);
//
//        if (parkIn.isBefore(nightToDay)) {
//            if (parkOut.isBefore(nightToDay)) {
//                list.add(new Interval(parkIn, parkOut));
//            } else {
//                list.add(new Interval(parkIn, nightToDay));
//                if (parkOut.isBefore(dayToNight)) {
//                    list.add(new Interval(nightToDay, parkOut));
//                } else {
//                    list.add(new Interval(nightToDay, dayToNight));
//                    list.add(new Interval(dayToNight, parkOut));
//                }
//            }
//        } else if (parkIn.isAfter(nightToDay) && parkIn.isBefore(dayToNight)) {
//            if (parkOut.isBefore(dayToNight)) {
//                list.add(new Interval(parkIn, parkOut));
//            } else {
//                list.add(new Interval(parkIn, dayToNight));
//                list.add(new Interval(dayToNight, parkOut));
//            }
//        } else if (parkIn.isAfter(dayToNight)) {
//            list.add(new Interval(parkIn, parkOut));
//        }
//
//        Interval[] result = new Interval[list.size()];
//        return list.toArray(result);
//    }
    private Interval[] splitFirstIntervalWhenIntervalLengthIsOne(Interval firstInterval) {
        int[] daySection = this.rule.getDayInterval();
        DateTime parkIn = new DateTime(firstInterval.getStart());
        DateTime parkOut = new DateTime(firstInterval.getEnd());

        List<Interval> list = new ArrayList<>();

        int year = firstInterval.getStart().getYear();
        int month = firstInterval.getStart().getMonthOfYear();
        int day = firstInterval.getStart().getDayOfMonth();
        int startHour = daySection[0] / 60;
        int startMinute = daySection[0] % 60;

        int endHour = daySection[1] / 60;
        int endMinute = daySection[1] % 60;

        // 计算白天、夜间各多少小时
        int howLongDay = endHour - startHour;
        int howLongNight = 24 - howLongDay;

        DateTime nightToDay = new DateTime(year, month, day, startHour, startMinute, 0);
        DateTime dayToNight = new DateTime(year, month, day, endHour, endMinute, 0);

        DateTime nextDnPoint = null, nextDnPoint1 = null;
        // 判断驶入时间在哪
        if (parkIn.isBefore(nightToDay)) {// 驶入时间在白天之前
            // 停车时间最分为3段，夜间--白天--夜间，其中第三段夜间跨天
            // 将需要切割的点计算出来，因为最多可分3段，所以需要再计算两个点，作为时间比较点
            nextDnPoint = nightToDay;
            nextDnPoint1 = nextDnPoint.plusHours(howLongDay);
        } else if (parkIn.isAfter(nightToDay) && parkIn.isBefore(dayToNight)) {// 驶入时间在白天与夜间之间
            // 停车时间最分为3段，白天--夜间--白天，其中第二段夜间跨天
            // 将需要切割的点计算出来，因为最多可分3段，所以需要再计算两个点，作为时间比较点
            nextDnPoint = dayToNight;
            nextDnPoint1 = nextDnPoint.plusHours(howLongNight);
        } else if (parkIn.isAfter(dayToNight)) {// 驶入时间在夜间之后
            // 停车时间最分为3段，夜间--白天--夜间，其中第一段夜间跨天
            // 将需要切割的点计算出来，因为最多可分3段，所以需要再计算两个点，作为时间比较点
            nextDnPoint = nightToDay.plusHours(24);
            nextDnPoint1 = nextDnPoint.plusHours(howLongDay);
        }
        if (parkOut.isBefore(nextDnPoint)) {// 只有一段
            list.add(new Interval(parkIn, parkOut));
        } else {// 有两段或者三段
            list.add(new Interval(parkIn, nextDnPoint));
            if (parkOut.isBefore(nextDnPoint1)) {// 说明有两段
                list.add(new Interval(nextDnPoint, parkOut));
            } else {// 说明有三段
                list.add(new Interval(nextDnPoint, nextDnPoint1));
                list.add(new Interval(nextDnPoint1, parkOut));
            }
        }

        Interval[] result = new Interval[list.size()];
        return list.toArray(result);
    }


    public static void main(String[] args) throws ParseException {
        // 更新时间毫秒并返回
        // System.out.println(DateTime.now());
        // System.out.println(DateTime.now().withMillisOfDay(1140 * 60 * 1000));
        // System.out.println(DateTime.now().plusDays(1));


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateTime parkIn = new DateTime(df.parse("2021-01-26 21:45:05"));
//        DateTime parkIn = new DateTime(df.parse("2021-01-26 05:45:05"));
        DateTime parkIn = new DateTime(df.parse("2021-01-26 09:45:05"));
//        DateTime parkOut = new DateTime(df.parse("2021-01-27 11:58:18"));
        DateTime parkOut = new DateTime(df.parse("2021-01-27 08:58:18"));

        List<Interval> list = new ArrayList<>();

        int year = 2021;
        int month = 1;
        int day = 26;
        int startHour = 7;
        int startMinute = 0;

        int endHour = 19;
        int endMinute = 0;


        // 计算白天、夜间各多少小时
        int howLongDay = endHour - startHour;
        int howLongNight = 24 - howLongDay;

        DateTime nightToDay = new DateTime(year, month, day, startHour, startMinute, 0);
        DateTime dayToNight = new DateTime(year, month, day, endHour, endMinute, 0);
        DateTime nextDnPoint = null, nextDnPoint1 = null;
        // 判断驶入时间在哪
        if (parkIn.isBefore(nightToDay)) {// 驶入时间在白天之前
            // 停车时间最分为3段，夜间--白天--夜间，其中第三段夜间跨天
            // 将需要切割的点计算出来，因为最多可分3段，所以需要再计算两个点，作为时间比较点
            nextDnPoint = nightToDay;
            nextDnPoint1 = nextDnPoint.plusHours(howLongDay);
//            System.out.println(DateUtil.formatDateYMDHMS(nextDnPoint.toDate()));
//            System.out.println(DateUtil.formatDateYMDHMS(nextDnPoint1.toDate()));
        } else if (parkIn.isAfter(nightToDay) && parkIn.isBefore(dayToNight)) {// 驶入时间在白天与夜间之间
            // 停车时间最分为3段，白天--夜间--白天，其中第二段夜间跨天
            // 将需要切割的点计算出来，因为最多可分3段，所以需要再计算两个点，作为时间比较点
            nextDnPoint = dayToNight;
            nextDnPoint1 = nextDnPoint.plusHours(howLongNight);
//            System.out.println(DateUtil.formatDateYMDHMS(nextDnPoint.toDate()));
//            System.out.println(DateUtil.formatDateYMDHMS(nextDnPoint1.toDate()));
        } else if (parkIn.isAfter(dayToNight)) {// 驶入时间在夜间之后
            // 停车时间最分为3段，夜间--白天--夜间，其中第一段夜间跨天
            // 将需要切割的点计算出来，因为最多可分3段，所以需要再计算两个点，作为时间比较点
            nextDnPoint = nightToDay.plusHours(24);
            nextDnPoint1 = nextDnPoint.plusHours(howLongDay);
//            System.out.println(DateUtil.formatDateYMDHMS(nextDnPoint.toDate()));
//            System.out.println(DateUtil.formatDateYMDHMS(nextDnPoint1.toDate()));
        }
        if (parkOut.isBefore(nextDnPoint)) {// 只有一段
            list.add(new Interval(parkIn, parkOut));
        } else {// 有两段或者三段
            list.add(new Interval(parkIn, nextDnPoint));
            if (parkOut.isBefore(nextDnPoint1)) {// 说明有两段
                list.add(new Interval(nextDnPoint, parkOut));
            } else {// 说明有三段
                list.add(new Interval(nextDnPoint, nextDnPoint1));
                list.add(new Interval(nextDnPoint1, parkOut));
            }
        }

//        if (parkIn.isBefore(nightToDay)) {
//            if (parkOut.isBefore(nightToDay)) {
//                list.add(new Interval(parkIn, parkOut));
//            } else {
//                list.add(new Interval(parkIn, nightToDay));
//                if (parkOut.isBefore(dayToNight)) {
//                    list.add(new Interval(nightToDay, parkOut));
//                } else {
//                    list.add(new Interval(nightToDay, dayToNight));
//                    list.add(new Interval(dayToNight, parkOut));
//                }
//            }
//        } else if (parkIn.isAfter(nightToDay) && parkIn.isBefore(dayToNight)) {
//            if (parkOut.isBefore(dayToNight)) {
//                list.add(new Interval(parkIn, parkOut));
//            } else {
//                list.add(new Interval(parkIn, dayToNight));
//                list.add(new Interval(dayToNight, parkOut));
//            }
//        } else if (parkIn.isAfter(dayToNight)) {
//            list.add(new Interval(parkIn, parkOut));
//        }

        Interval[] result = new Interval[list.size()];

        System.out.println(result);

    }


    public ParkingInfo getParkingInfo() {
        return this.parkingInfo;
    }


    public void setParkingInfo(ParkingInfo parkingInfo) {
        this.parkingInfo = parkingInfo;
    }


    public Rule getRule() {
        return this.rule;
    }


    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
