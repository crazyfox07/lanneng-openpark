package com.lann.openpark.charge.bizobj.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadableInterval;

/**
 * 时段规则算法
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:40
 **/
public class PeriodChargeRule {
    private List<PeriodUnit> peroidList = new ArrayList<>();


    public void addPeriodUnit(int startMiniteOfDay, int endMiniteOfDay, int timeUnit, double unitCost) {
        if (startMiniteOfDay < 0 || endMiniteOfDay < 0 || timeUnit < 0 || unitCost < 0.0D) {
            throw new RuntimeException();
        }
        PeriodUnit unit = new PeriodUnit(startMiniteOfDay, endMiniteOfDay, timeUnit, unitCost);
        this.peroidList.add(unit);
    }


    public int queryDistance2Entire(DateTime start, DateTime end) {
        int total = 0;
        int inMinute = start.getMinuteOfDay();

        DateTime periodStartDateTime = null;
        DateTime periodEndDateTime = null;
        PeriodUnit selectedPeriod = null;

        for (PeriodUnit period : this.peroidList) {
            int periodStartMinute = period.getStart();
            int periodEndMinute = period.getEnd();
            if (periodStartMinute < periodEndMinute) {
                if (inMinute >= periodStartMinute && inMinute < periodEndMinute) {
                    periodStartDateTime = start.withMillisOfDay(periodStartMinute * 60 * 1000);
                    periodEndDateTime = start.withMillisOfDay(periodEndMinute * 60 * 1000);
                    selectedPeriod = period;
                    break;
                }
                continue;
            }
            if (inMinute >= periodStartMinute && inMinute < 1440) {
                periodStartDateTime = start.withMillisOfDay(periodStartMinute * 60 * 1000);
                periodEndDateTime = start.withMillisOfDay(periodEndMinute * 60 * 1000).plusDays(1);
                selectedPeriod = period;
                break;
            }
            if (inMinute >= 0 && inMinute < periodEndMinute) {
                periodStartDateTime = start.withMillisOfDay(periodStartMinute * 60 * 1000).minusDays(1);
                periodEndDateTime = start.withMillisOfDay(periodEndMinute * 60 * 1000);
                selectedPeriod = period;


                break;
            }
        }

        Interval periodInterval = new Interval((ReadableInstant) periodStartDateTime, (ReadableInstant) periodEndDateTime);
        Interval lap = (new Interval((ReadableInstant) start, (ReadableInstant) end)).overlap((ReadableInterval) periodInterval);
        int length = 0;
        if (lap != null) {
            length = (int) lap.toDurationMillis() / 60000;
        }
        if (selectedPeriod == null) {
            return 0;
        }
        if (0 == selectedPeriod.getTimeUnit() || length % selectedPeriod.getTimeUnit() == 0) {
            total = 0;
        } else {
            total = selectedPeriod.getTimeUnit() - length % selectedPeriod.getTimeUnit();
        }
        return total;
    }

    /**
     * 根据时段计算费用
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/24 8:49
     **/
    public double getCost(DateTime start, DateTime end) {
        boolean isDriveCrossDay = false;// 停车时间有无跨天，(20,4] 20点进去，第二天8点出去

        double total = 0.0D;// 总费用

        int inMinute = start.getMinuteOfDay();// 获取驶入时间在一天中的分钟数
        int outMinute = end.getMinuteOfDay();// 获取驶离时间在一天中的分钟数

        if (inMinute > outMinute) {// 跨天
            outMinute += 1440;
            isDriveCrossDay = true;
        }

        // 下面从第一个有交集的时段开始计算。
        for (PeriodUnit period : this.peroidList) {
            int periodStartMinute = period.getStart();
            int periodEndMinute = period.getEnd();

            // 规则区间跨天处理
            if (periodStartMinute > periodEndMinute) {
                periodEndMinute = 1440 + periodEndMinute;
                // 如果本次停车跨天了
                if (isDriveCrossDay) {
                    // 如果驶入在该区间，则计算，不在就不用计算
                    if (periodStartMinute <= inMinute) {// 判断是否在本时段的方法（时段开始时间小于驶入时间）
                        int tempOutMinute = Math.min(outMinute, periodEndMinute);// 计费结束时间，取驶离时间和时段结束时间的小值
                        total += getPeriodCost(period, inMinute, tempOutMinute);
                        if (tempOutMinute < outMinute) {// 递归继续取值，总有一个时间段能匹配到
                            total += getCost(end.minusMinutes(outMinute - tempOutMinute), end);
                        }
                    }
                } else {// 本次停车没有跨天
                    if (periodStartMinute <= inMinute) {
                        int tempOutMinute = Math.min(outMinute, periodEndMinute);
                        total += getPeriodCost(period, inMinute, tempOutMinute);
                        if (tempOutMinute < outMinute) {// 递归继续取值，总有一个时间段能匹配到
                            total += getCost(end.minusMinutes(outMinute - tempOutMinute), end);
                        }
                    } else {
                        period.setStart(0);
                        if (inMinute >= 0 && inMinute < periodEndMinute - 1440) {
                            int tempOutMinute = Math.min(outMinute, periodEndMinute - 1440);
                            total += getPeriodCost(period, inMinute, tempOutMinute);
                            if (tempOutMinute < outMinute) {
                                total += getCost(end.minusMinutes(outMinute - tempOutMinute), end);
                            }
                        }
                    }
                }
                // 规则不跨天处理，很简单，下面逻辑完成
            } else {
                // 对于区间不跨天来说，只要驶入时间在其中，就有重叠部分
                if (periodStartMinute <= inMinute && inMinute < periodEndMinute) {
                    int tempOutMinute = Math.min(outMinute, periodEndMinute);
                    total += getPeriodCost(period, inMinute, tempOutMinute);
                    if (tempOutMinute < outMinute) {
                        total += getCost(end.minusMinutes(outMinute - tempOutMinute), end);
                    }
                }
            }
        }

        return total;
    }


    private double getPeriodCost(PeriodUnit period, int inMinute, int outMinute) {
        double total = 0.0D;
        int parkLength = outMinute - inMinute;

        if (parkLength % period.getTimeUnit() == 0) {
            total += (parkLength / period.getTimeUnit()) * period.getUnitCost();
        } else {
            total += (parkLength / period.getTimeUnit() + 1) * period.getUnitCost();
        }
        return total;
    }


    public List<PeriodUnit> getPeroidList() {
        return this.peroidList;
    }


    public void setPeroidList(List<PeriodUnit> peroidList) {
        this.peroidList = peroidList;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < this.peroidList.size(); i++) {
            sb.append("时段配置").append(i + 1).append(": ").append(((PeriodUnit) this.peroidList.get(i)).toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateTime d1 = new DateTime(df.parse("2017-02-29 1:16:00"));
        DateTime d2 = new DateTime(df.parse("2017-02-29 2:00:00"));

        PeriodChargeRule pcr = new PeriodChargeRule();
        pcr.addPeriodUnit(1380, 60, 30, 1.0D);
        pcr.addPeriodUnit(60, 120, 15, 1.0D);
        System.out.println(pcr.queryDistance2Entire(d1, d2));
    }
}

