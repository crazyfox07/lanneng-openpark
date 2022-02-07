package com.lann.openpark.charge.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.bizobj.Rule;
import com.lann.openpark.charge.bizobj.config.*;
import com.lann.openpark.charge.dao.entiy.Charge2Policy;
import com.lann.openpark.charge.dao.mapper.ChargePlanConfigMapper;
import com.lann.openpark.charge.dao.repository.Charge2PolicyRepository;
import com.lann.openpark.charge.logic.ParkingAlgorithm;
import com.lann.openpark.util.DateUtil;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 停车计算费用
 *
 * @Author songqiang
 * @Description
 * @Date 2020/7/7 15:04
 **/
@Service
public class ChargeService {
    private static final Logger log = LoggerFactory.getLogger(ChargeService.class);

    @Autowired
    Charge2PolicyRepository charge2PolicyRepository;

    @Autowired
    ChargePlanConfigMapper chargePlanConfigMapper;

    /**
     * 根据驶入时间、驶离时间、车牌类型计算停车费用
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 15:13
     **/
    public ParkingInfo getParkingFee(ParkingInfo parkingInfo, String policyId, boolean isFreeTime) throws Exception {
        double fee = 0;// 停车费用
        // 查询计费规则
        Charge2Policy charge2Policy = charge2PolicyRepository.findChar2PolicyByNid(policyId);
        if (null == charge2Policy) {// 没有查询到计费规则
            log.error("【查询计费规则】--根据计费规则ID查询计费规则为空");
            parkingInfo.setCost(0);
            return parkingInfo;
        }
        String template = charge2Policy.getConfigTemplate();// 计费规则配置json串
        JSONObject json = JSONObject.parseObject(template);
        // 生成计费规则
        Rule rule = new Rule();
        List<LocalTime[]> freeTimeSections = getFreeTimeSectionsByJson(json.getJSONArray("freeTimeSections"));
        rule.setFreeTimeSections(freeTimeSections);
        FreeTimeConfig freeTimeConfig = getFreeTimeConfigByJson(json.getJSONObject("freeTimeConfig"));

        // 控制是否有免费时间
        if (isFreeTime) {
            freeTimeConfig.setFreeTime(0);
            freeTimeConfig.setFreeLimitTime(0);
        }

        rule.setFreeTimeConfig(freeTimeConfig);
        rule = getCaclRule(rule, json.getJSONObject("dayNightConfig"));
        BorrowTimeConfig borrowTimeConfig = getBorrowTimeConfigByJson(json.getJSONObject("borrowTimeConfig"));
        rule.setBorrowTimeConfig(borrowTimeConfig);
        LoopChargeConfig loopConfig = getLoopChargeConfigByJson(json.getJSONObject("loopConfig"));
        rule.setLoopConfig(loopConfig);
        rule.setLimitCharge(json.getDoubleValue("limitCharge"));
        AttachRule attachRule = getAttachRuleByJson(json.getJSONObject("attachRule"));
        rule.setAttachRule(attachRule);

        // 生成计费逻辑
        ParkingAlgorithm algorithm = new ParkingAlgorithm(parkingInfo, rule);
        // 计费
        algorithm.executeCalculate();
        // 获得停车费用
        // Double orderFee = Double.valueOf(algorithm.getParkingInfo().getCost());
        // 跨配置白天黑夜，大于限额，费用置为限额
//        int days = Days.daysBetween(parkingInfo.getCalcDriveIn(), parkingInfo.getCaclDriveOut()).getDays();
//        double limit = rule.getLimitCharge();
//        if (days == 0 && limit < algorithm.getParkingInfo().getCost()) {
//            log.info("费用大于限额了：" + algorithm.getParkingInfo().getCost());
//            algorithm.getParkingInfo().setCost(limit);
//        }
        return algorithm.getParkingInfo();
    }

    private List<LocalTime[]> getFreeTimeSectionsByJson(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<LocalTime[]> freeTimeSections = (List) new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            int beginMinute = json.getIntValue("beginMinute");
            int endMinute = json.getIntValue("endMinute");
            LocalTime localTime = new LocalTime();
            LocalTime beginLocalTime = localTime.withMillisOfDay(beginMinute * 60 * 1000);
            LocalTime endMinuteLocalTime = localTime.withMillisOfDay(endMinute * 60 * 1000);
            LocalTime[] section = {beginLocalTime, endMinuteLocalTime};
            freeTimeSections.add(section);
        }
        if (freeTimeSections.size() > 0) {
            return freeTimeSections;
        }
        return null;
    }

    private FreeTimeConfig getFreeTimeConfigByJson(JSONObject json) {
        FreeTimeConfig config = new FreeTimeConfig();
        if (json == null) {
            return config;
        }
        String freeMode = json.getString("freeMode");
        if ("4001".equals(freeMode)) {
            config.setFreeMode("A0105");
        } else {
            config.setFreeMode("A0106");
        }
        config.setFreeTime(json.getIntValue("freeTime"));
        config.setFreeLimitTime(json.getIntValue("freeLimitTime"));

        String allDayAvailable = json.getString("allDayAvailable");

        if (allDayAvailable != null) {
            config.setAllDayAvailable(Boolean.valueOf(allDayAvailable).booleanValue());
        } else {
            String dayAvailable = json.getString("dayAvailable");
            String nightAvailable = json.getString("nightAvailable");
            if (dayAvailable != null) {
                config.setDayAvailable(Boolean.valueOf(dayAvailable).booleanValue());
            }
            if (nightAvailable != null) {
                config.setNightAvailable(Boolean.valueOf(nightAvailable).booleanValue());
            }
        }
        return config;
    }
      

    private Rule getCaclRule(Rule rule, JSONObject json) {
        if (json == null) {
            System.out.println("【计费算法拼装】：Exception,无法默认太多的配置");
            return rule;
        }
        String caclType = json.getString("type");
        try {
            if ("6501".equals(caclType)) {
                rule.setCalcType("A0109");
            } else {
                JSONObject o = json.getJSONObject("dayInterval");
                rule.setCalcType("A0110");
                rule.setDayInterval(new int[]{o.getIntValue("beginMinute"), o.getIntValue("endMinute")});
            }
        } catch (Exception e) {
            this.log.error("抛出异常", e);
            return rule;
        }

        ChargeRuleUnit allDayChargeConfig = getChargeRuleUnitByJson(json.getJSONObject("allDayChargeConfig"));
        ChargeRuleUnit dayChargeConfig = getChargeRuleUnitByJson(json.getJSONObject("dayChargeConfig"));
        ChargeRuleUnit nightChargeConfig = getChargeRuleUnitByJson(json.getJSONObject("nightChargeConfig"));

        rule.setAllDayChargeConfig(allDayChargeConfig);
        rule.setDayChargeConfig(dayChargeConfig);
        rule.setNightChargeConfig(nightChargeConfig);
        return rule;
    }

    private ChargeRuleUnit getChargeRuleUnitByJson(JSONObject json) {
        if (json == null) {
            return null;
        }
        ChargeRuleUnit chargeRuleUnit = new ChargeRuleUnit();
        String type = json.getString("type");
        if (type.equals("6301")) {
            PeriodChargeRule periodRule = new PeriodChargeRule();
            JSONArray array = json.getJSONArray("periodRule");
            for (int i = 0; i < array.size(); i++) {
                JSONObject o = array.getJSONObject(i);
                int beginMinute = o.getIntValue("beginMinute");
                int endMinute = o.getIntValue("endMinute");
                int timeUnit = o.getIntValue("unitMinute");
                Double unitCost = Double.valueOf(o.getDoubleValue("unitPrice"));
                periodRule.addPeriodUnit(beginMinute, endMinute, timeUnit, unitCost.doubleValue());
            }
            chargeRuleUnit.setPeriodRule(periodRule);
        } else {
            DurationChargeRule duractionRule = new DurationChargeRule();
            JSONArray array = json.getJSONArray("duractionRule");
            for (int i = 0; i < array.size(); i++) {
                JSONObject o = array.getJSONObject(i);
                int beginMinute = o.getIntValue("beginMinute");
                int endMinute = o.getIntValue("endMinute");
                int timeUnit = o.getIntValue("unitMinute");
                Double unitCost = Double.valueOf(o.getDoubleValue("unitPrice"));
                duractionRule.addDuractionUnit(beginMinute, endMinute, timeUnit, unitCost.doubleValue());
            }
            chargeRuleUnit.setDuractionRule(duractionRule);
        }
        return chargeRuleUnit;
    }


    private AttachRule getAttachRuleByJson(JSONObject json) {
        AttachRule attachRule = new AttachRule();
        if (json == null) {
            return attachRule;
        }

        attachRule.setAttachMoney(json.getDoubleValue("attachMoney"));
        JSONObject attachPeriod = json.getJSONObject("attachPeriod");
        if (attachPeriod != null) {
            attachRule.setValidParkMinutes(attachPeriod.getIntValue("validParkMinutes"));
            JSONArray array = attachPeriod.getJSONArray("periods");
            for (int i = 0; i < array.size(); i++) {
                JSONObject o = array.getJSONObject(i);
                int beginMinute = o.getIntValue("beginMinute");
                int endMinute = o.getIntValue("endMinute");
                int timeUnit = o.getIntValue("unitMinute");
                Double unitCost = Double.valueOf(o.getDoubleValue("unitPrice"));
                attachRule.addPeriodUnit(beginMinute, endMinute, timeUnit, unitCost.doubleValue());
            }
        }
        return attachRule;
    }


    private LoopChargeConfig getLoopChargeConfigByJson(JSONObject json) {
        LoopChargeConfig loopConfig = new LoopChargeConfig();
        if (json == null) {
            return loopConfig;
        }
        String loopType = json.getString("loopType");
        if ("6101".equals(loopType)) {
            loopConfig.setLoopType("A0108");
        } else {
            loopConfig.setLoopType("A0107");
        }
        loopConfig.setCustomPoint(json.getIntValue("customPoint"));
        return loopConfig;
    }

    private BorrowTimeConfig getBorrowTimeConfigByJson(JSONObject json) {
        BorrowTimeConfig borrowTimeConfig = new BorrowTimeConfig();
        if (json == null) {
            return borrowTimeConfig;
        }
        borrowTimeConfig.setAllDayTimeCanBeTaken(json.getBooleanValue("allDayTimeCanBeTaken"));
        borrowTimeConfig.setDayTimeCanBeTaken(json.getBooleanValue("dayTimeCanBeTaken"));
        borrowTimeConfig.setNightTimeCanBeTaken(json.getBooleanValue("nightTimeCanBeTaken"));
        borrowTimeConfig.setTimeLimit(json.getIntValue("timeLimit"));
        return borrowTimeConfig;
    }

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(DateUtil.formatDateYMDHMS(new Date()));
    }
}
