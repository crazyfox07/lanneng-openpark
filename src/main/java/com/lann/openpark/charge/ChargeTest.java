package com.lann.openpark.charge;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lann.openpark.charge.bizobj.Rule;
import com.lann.openpark.charge.bizobj.config.*;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChargeTest {
    private static final Logger log = LoggerFactory.getLogger(ChargeTest.class);

    public Rule getRuleByPolicyId(String policyId) throws Exception {

        Rule rule = new Rule();

        // 测试收费策略
        // 1.全天收费按时长
        // String template = "{\"freeTimeSections\":[],\"freeTimeConfig\":{\"freeTime\":30,\"freeMode\":\"4001\",\"allDayAvailable\":true},\"borrowTimeConfig\":{\"allDayTimeCanBeTaken\":true},\"dayNightConfig\":{\"type\":\"6501\",\"allDayChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":300,\"unitPrice\":1,\"unitMinute\":60},{\"beginMinute\":300,\"endMinute\":1440,\"unitPrice\":3,\"unitMinute\":60}]}},\"loopConfig\":{\"loopType\":\"6101\"},\"limitCharge\":20,\"attachRule\":{\"attachMoney\":0,\"attachPeriod\":{\"validParkMinutes\":\"0\",\"periods\":[]}}}";
        //        // 2.全天收费按时段
        //        // String template ="{\"freeTimeSections\":[],\"freeTimeConfig\":{\"freeTime\":30,\"freeMode\":\"4001\",\"allDayAvailable\":true},\"borrowTimeConfig\":{\"allDayTimeCanBeTaken\":true},\"dayNightConfig\":{\"type\":\"6501\",\"allDayChargeConfig\":{\"type\":\"6301\",\"periodRule\":[{\"beginMinute\":0,\"endMinute\":420,\"unitPrice\":1,\"unitMinute\":60},{\"beginMinute\":420,\"endMinute\":1080,\"unitPrice\":3,\"unitMinute\":60},{\"beginMinute\":1080,\"endMinute\":0,\"unitPrice\":1,\"unitMinute\":60}]}},\"loopConfig\":{\"loopType\":\"6102\",\"customPoint\":\"0\"},\"limitCharge\":20,\"attachRule\":{\"attachMoney\":0,\"attachPeriod\":{\"validParkMinutes\":\"0\",\"periods\":[]}}}";
        //        // 3.白天黑夜按时段
        //        // String template = "{\"freeTimeSections\":[],\"freeTimeConfig\":{\"freeTime\":30,\"freeMode\":\"4001\",\"dayAvailable\":true,\"nightAvailable\":true},\"borrowTimeConfig\":{\"timeLimit\":60,\"dayTimeCanBeTaken\":true,\"nightTimeCanBeTaken\":true},\"dayNightConfig\":{\"type\":\"6502\",\"dayInterval\":{\"beginMinute\":420,\"endMinute\":1140},\"dayChargeConfig\":{\"type\":\"6301\",\"periodRule\":[{\"beginMinute\":420,\"endMinute\":1140,\"unitPrice\":3,\"unitMinute\":60}]},\"nightChargeConfig\":{\"type\":\"6301\",\"periodRule\":[{\"beginMinute\":1140,\"endMinute\":420,\"unitPrice\":1,\"unitMinute\":60}]}},\"loopConfig\":{\"loopType\":\"6102\",\"customPoint\":\"0\"},\"limitCharge\":20,\"attachRule\":{\"attachMoney\":0,\"attachPeriod\":{\"validParkMinutes\":\"0\",\"periods\":[]}}}";
        //        // 4.正式系统收费规则，按时长，晚上不借
        //        // String template = "{\"freeTimeSections\":[],\"freeTimeConfig\":{\"freeTime\":30,\"freeMode\":\"4001\",\"dayAvailable\":true,\"nightAvailable\":true},\"borrowTimeConfig\":{\"timeLimit\":60,\"dayTimeCanBeTaken\":true,\"nightTimeCanBeTaken\":false},\"dayNightConfig\":{\"type\":\"6502\",\"dayInterval\":{\"beginMinute\":420,\"endMinute\":1140},\"dayChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":720,\"unitPrice\":3,\"unitMinute\":60}]},\"nightChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":60,\"unitPrice\":1.5,\"unitMinute\":60},{\"beginMinute\":60,\"endMinute\":720,\"unitPrice\":1.5,\"unitMinute\":660}]}},\"loopConfig\":{\"loopType\":\"6102\",\"customPoint\":\"7\"},\"limitCharge\":40,\"attachRule\":{\"attachMoney\":0,\"attachPeriod\":{\"validParkMinutes\":\"0\",\"periods\":[]}}}";
        //        // 5.正式系统收费规则，按时长，晚上借
        // String template = "{\"freeTimeSections\":[],\"freeTimeConfig\":{\"freeTime\":30,\"freeMode\":\"4001\",\"dayAvailable\":true,\"nightAvailable\":true},\"borrowTimeConfig\":{\"timeLimit\":60,\"dayTimeCanBeTaken\":true,\"nightTimeCanBeTaken\":true},\"dayNightConfig\":{\"type\":\"6502\",\"dayInterval\":{\"beginMinute\":420,\"endMinute\":1140},\"dayChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":720,\"unitPrice\":3,\"unitMinute\":60}]},\"nightChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":60,\"unitPrice\":1.5,\"unitMinute\":60},{\"beginMinute\":60,\"endMinute\":720,\"unitPrice\":1.5,\"unitMinute\":660}]}},\"loopConfig\":{\"loopType\":\"6102\",\"customPoint\":\"7\"},\"limitCharge\":40,\"attachRule\":{\"attachMoney\":0,\"attachPeriod\":{\"validParkMinutes\":\"0\",\"periods\":[]}}}";
        String template = "{\"freeTimeSections\":[],\"freeTimeConfig\":{\"freeTime\":30,\"freeMode\":\"4001\",\"dayAvailable\":true,\"nightAvailable\":true},\"borrowTimeConfig\":{\"timeLimit\":60,\"dayTimeCanBeTaken\":true,\"nightTimeCanBeTaken\":true},\"dayNightConfig\":{\"type\":\"6502\",\"dayInterval\":{\"beginMinute\":420,\"endMinute\":1200},\"dayChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":780,\"unitPrice\":3,\"unitMinute\":60}]},\"nightChargeConfig\":{\"type\":\"6302\",\"duractionRule\":[{\"beginMinute\":0,\"endMinute\":660,\"unitPrice\":1.5,\"unitMinute\":60}]}},\"loopConfig\":{\"loopType\":\"6102\",\"customPoint\":\"7\"},\"limitCharge\":30,\"attachRule\":{\"attachMoney\":0,\"attachPeriod\":{\"validParkMinutes\":\"0\",\"periods\":[]}}}";
        JSONObject json = JSONObject.parseObject(template);
        List<LocalTime[]> freeTimeSections = getFreeTimeSectionsByJson(json.getJSONArray("freeTimeSections"));
        rule.setFreeTimeSections(freeTimeSections);


        FreeTimeConfig freeTimeConfig = getFreeTimeConfigByJson(json.getJSONObject("freeTimeConfig"));
        rule.setFreeTimeConfig(freeTimeConfig);


        rule = getCaclRule(rule, json.getJSONObject("dayNightConfig"));


        BorrowTimeConfig borrowTimeConfig = getBorrowTimeConfigByJson(json.getJSONObject("borrowTimeConfig"));
        rule.setBorrowTimeConfig(borrowTimeConfig);


        LoopChargeConfig loopConfig = getLoopChargeConfigByJson(json.getJSONObject("loopConfig"));
        rule.setLoopConfig(loopConfig);


        rule.setLimitCharge(json.getIntValue("limitCharge"));


        AttachRule attachRule = getAttachRuleByJson(json.getJSONObject("attachRule"));
        rule.setAttachRule(attachRule);
        return rule;
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


//    public Map<String, String> queryOrderFee(String parkCode, String plateNo, String plateType, Date timeInDate, Date timeOutDdate) throws Exception {
//        Map<String, String> map = new HashMap<>();
//
//        try {
//            if (plateType == null || plateType.trim().length() <= 0) {
//                plateType = "7201";
//            }
//            map.put("consAll", "0");
//            map.put("payFee", "0");
//
//
//            Double consAll = Double.valueOf(0.0D);
//            Double payFee = Double.valueOf(0.0D);
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String beginTimeStr = sdf.format(timeInDate);
//            String endTimeStr = sdf.format(timeOutDdate);
//
//
//            if (StringUtil.equals(beginTimeStr, endTimeStr)) {
//
//
//                List<ChargePlanConfig> configList = new ArrayList<>();
//                configList = this.chargePlanConfigMapper.selectByDateParkPlate(beginTimeStr, parkCode, plateType);
//
//                if (configList != null && configList.size() > 0) {
//
//                    String policyId = ((ChargePlanConfig) configList.get(0)).getPolicyId();
//                    Map<String, Double> subMap = new HashMap<>();
//                    subMap = getSubChargeMap(policyId, timeInDate, timeOutDdate, plateNo, plateType, parkCode);
//
//                    consAll = subMap.get("subTotal");
//                    payFee = subMap.get("subPayFee");
//
//                    map.put("consAll", String.valueOf(NumberUtils.doubleRoundTwoBit(consAll.doubleValue())));
//                    map.put("payFee", String.valueOf(NumberUtils.doubleRoundTwoBit(payFee.doubleValue())));
//                    return map;
//                }
//
//                map.put("consAll", "0.0");
//                map.put("payFee", "0.0");
//                return map;
//            }
//
//
//            List<OrderChargeTimeVO> orderChargeTimeVOList = new ArrayList<>();
//            orderChargeTimeVOList = getOrderChargeTimeVOList(timeInDate, timeOutDdate, parkCode, plateNo, plateType);
//
//            if (orderChargeTimeVOList.size() == 0) {
//
//                map.put("consAll", "0.0");
//                map.put("payFee", "0.0");
//                return map;
//            }
//
//
//            List<OrderChargeTimeSectionVO> orderChargeTimeSectionList = new ArrayList<>();
//            orderChargeTimeSectionList = getOrderChargeTimeSectionList(orderChargeTimeVOList, timeInDate, timeOutDdate);
//
//
//            if (orderChargeTimeSectionList.size() == 0) {
//
//                map.put("consAll", "0.0");
//                map.put("payFee", "0.0");
//                return map;
//            }
//
//
//            for (int i = 0; i < orderChargeTimeSectionList.size(); i++) {
//
//                OrderChargeTimeSectionVO orderChargeTimeSectionVO = orderChargeTimeSectionList.get(i);
//                Map<String, Double> subMap = new HashMap<>();
//                subMap = getSubChargeMap(orderChargeTimeSectionVO.getPolicyId(), orderChargeTimeSectionVO
//                        .getTimeSectionBegin(), orderChargeTimeSectionVO
//                        .getTimeSectionEnd(), plateNo, plateType, parkCode);
//
//                consAll = Double.valueOf(consAll.doubleValue() + ((Double) subMap.get("subTotal")).doubleValue());
//                payFee = Double.valueOf(payFee.doubleValue() + ((Double) subMap.get("subPayFee")).doubleValue());
//            }
//            map.put("consAll", String.valueOf(NumberUtils.doubleRoundTwoBit(consAll.doubleValue())));
//            map.put("payFee", String.valueOf(NumberUtils.doubleRoundTwoBit(payFee.doubleValue())));
//        } catch (Exception e) {
//            this.log.info("获取订单计费queryOrderFee,发生异常，Exception=" + e);
//            throw e;
//        }
//        return map;
//    }


//    private Map<String, String> getCalcParkFee(Date beginTime, Date endTime, String plateNo, String plateType, String parkCode) throws Exception {
//        Map<String, String> map = new HashMap<>();
//
//        try {
//            if (StringUtil.isEmpty(plateType)) {
//                plateType = "7201";
//            }
//
//            map.put("consAll", "0");
//            map.put("payFee", "0");
//
//
//            if (beginTime == null || endTime == null || plateNo == null || plateType == null) {
//                return map;
//            }
//
//            Date timeInDate = beginTime;
//            Date timeOutDdate = endTime;
//
//            Double consAll = Double.valueOf(0.0D);
//            Double payFee = Double.valueOf(0.0D);
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String beginTimeStr = sdf.format(timeInDate);
//            String endTimeStr = sdf.format(timeOutDdate);
//
//
//            String plateTypeCarType = plateType;
//
//
//            switch (plateType) {
//                case "7202":
//                    plateTypeCarType = "7202";
//                    break;
//                case "7204":
//                case "7205":
//                    plateTypeCarType = plateType;
//                    break;
//                default:
//                    plateTypeCarType = "7201";
//                    break;
//            }
//
//            if (StringUtil.equals(beginTimeStr, endTimeStr)) {
//
//                List<ChargePlanConfig> configList = new ArrayList<>();
//                configList = this.chargePlanConfigMapper.selectByDateParkPlate(beginTimeStr, parkCode, plateTypeCarType);
//
//                if (configList != null && configList.size() > 0) {
//
//                    String policyId = ((ChargePlanConfig) configList.get(0)).getPolicyId();
//                    Map<String, Double> subMap = new HashMap<>();
//                    subMap = getSubChargeMap(policyId, timeInDate, timeOutDdate, plateNo, plateType, parkCode);
//
//                    consAll = subMap.get("subTotal");
//                    payFee = subMap.get("subPayFee");
//
//                    map.put("consAll", String.valueOf(consAll));
//                    map.put("payFee", String.valueOf(payFee));
//                    return map;
//                }
//
//                map.put("consAll", "0.0");
//                map.put("payFee", "0.0");
//                return map;
//            }
//
//
//            List<OrderChargeTimeVO> orderChargeTimeVOList = new ArrayList<>();
//            orderChargeTimeVOList = getOrderChargeTimeVOList(timeInDate, timeOutDdate, parkCode, plateNo, plateTypeCarType);
//
//            if (orderChargeTimeVOList.size() <= 0) {
//
//                map.put("consAll", "0.0");
//                map.put("payFee", "0.0");
//                return map;
//            }
//
//
//            List<OrderChargeTimeSectionVO> orderChargeTimeSectionList = new ArrayList<>();
//            orderChargeTimeSectionList = getOrderChargeTimeSectionList(orderChargeTimeVOList, timeInDate, timeOutDdate);
//
//
//            if (orderChargeTimeSectionList == null) {
//
//                map.put("consAll", "0.0");
//                map.put("payFee", "0.0");
//                return map;
//            }
//
//
//            for (int i = 0; i < orderChargeTimeSectionList.size(); i++) {
//
//                OrderChargeTimeSectionVO orderChargeTimeSectionVO = orderChargeTimeSectionList.get(i);
//                Map<String, Double> subMap = new HashMap<>();
//                subMap = getSubChargeMap(orderChargeTimeSectionVO.getPolicyId(), orderChargeTimeSectionVO
//                        .getTimeSectionBegin(), orderChargeTimeSectionVO
//                        .getTimeSectionEnd(), plateNo, plateType, parkCode);
//
//                consAll = Double.valueOf(consAll.doubleValue() + ((Double) subMap.get("subTotal")).doubleValue());
//                payFee = Double.valueOf(payFee.doubleValue() + ((Double) subMap.get("subPayFee")).doubleValue());
//            }
//
//            map.put("consAll", String.valueOf(consAll));
//            map.put("payFee", String.valueOf(payFee));
//        } catch (Exception e) {
//            this.log.info("获取订单计费getCalcParkFee,发生异常，Exception=" + e);
//            throw e;
//        }
//        return map;
//    }


//    private List<OrderChargeTimeSectionVO> getOrderChargeTimeSectionList(List<OrderChargeTimeVO> orderChargeTimeList, Date timeIn, Date timeOut) throws ParseException {
//        List<OrderChargeTimeSectionVO> orderChargeTimeSectionList = new ArrayList<>();
//
//        OrderChargeTimeSectionVO orderChargeTimeSectionVO = new OrderChargeTimeSectionVO();
//
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat sdf3 = new SimpleDateFormat(" HH:mm:ss");
//
//        String timeInHms = sdf3.format(timeIn);
//
//        String timeOutHms = sdf3.format(timeOut);
//
//        if (orderChargeTimeList.size() > 0) {
//
//            orderChargeTimeSectionVO.setTimeSectionBegin(sdf2.parse(sdf.format(((OrderChargeTimeVO) orderChargeTimeList.get(0)).getParkDate()) + timeInHms));
//            orderChargeTimeSectionVO.setPolicyId(((OrderChargeTimeVO) orderChargeTimeList.get(0)).getPolicyId());
//            orderChargeTimeSectionVO.setIsFree(((OrderChargeTimeVO) orderChargeTimeList.get(0)).getIsFree());
//
//            if (orderChargeTimeList.size() == 1) {
//                orderChargeTimeSectionVO.setTimeSectionEnd(sdf2.parse(sdf.format(((OrderChargeTimeVO) orderChargeTimeList.get(0)).getParkDate()) + timeOutHms));
//                orderChargeTimeSectionVO.setPolicyId(((OrderChargeTimeVO) orderChargeTimeList.get(0)).getPolicyId());
//                orderChargeTimeSectionVO.setIsFree(((OrderChargeTimeVO) orderChargeTimeList.get(0)).getIsFree());
//                orderChargeTimeSectionList.add(orderChargeTimeSectionVO);
//            } else {
//
//                for (int i = 0; i < orderChargeTimeList.size(); i++) {
//                    if (i < orderChargeTimeList.size() - 1) {
//
//                        OrderChargeTimeVO currentVO = orderChargeTimeList.get(i);
//                        OrderChargeTimeVO nextVO = orderChargeTimeList.get(i + 1);
//
//                        if (StringUtil.equals(currentVO.getPolicyId(), nextVO.getPolicyId()) && currentVO
//                                .getIsFree() == nextVO.getIsFree()) {
//                            orderChargeTimeSectionVO.setTimeSectionEnd(sdf2.parse(sdf.format(nextVO.getParkDate()) + " 23:59:59"));
//                        } else {
//                            orderChargeTimeSectionVO.setTimeSectionEnd(sdf2.parse(sdf.format(currentVO.getParkDate()) + " 23:59:59"));
//                            orderChargeTimeSectionList.add(orderChargeTimeSectionVO);
//
//                            orderChargeTimeSectionVO = new OrderChargeTimeSectionVO();
//                            orderChargeTimeSectionVO.setTimeSectionBegin(sdf2.parse(sdf.format(nextVO.getParkDate()) + " 00:00:00"));
//                            orderChargeTimeSectionVO.setPolicyId(nextVO.getPolicyId());
//                            orderChargeTimeSectionVO.setIsFree(nextVO.getIsFree());
//                        }
//                    } else {
//
//                        OrderChargeTimeVO currentVO = orderChargeTimeList.get(i);
//                        OrderChargeTimeVO previousVO = orderChargeTimeList.get(i - 1);
//
//                        if (StringUtil.equals(previousVO.getPolicyId(), currentVO.getPolicyId()) && previousVO
//                                .getIsFree() == currentVO.getIsFree()) {
//                            orderChargeTimeSectionVO.setTimeSectionEnd(sdf2.parse(sdf.format(currentVO.getParkDate()) + timeOutHms));
//                            orderChargeTimeSectionVO.setPolicyId(currentVO.getPolicyId());
//                            orderChargeTimeSectionVO.setIsFree(currentVO.getIsFree());
//                            orderChargeTimeSectionList.add(orderChargeTimeSectionVO);
//                        } else {
//                            orderChargeTimeSectionVO = new OrderChargeTimeSectionVO();
//                            orderChargeTimeSectionVO.setTimeSectionBegin(sdf2.parse(sdf.format(currentVO.getParkDate()) + " 00:00:00"));
//                            orderChargeTimeSectionVO.setTimeSectionEnd(sdf2.parse(sdf.format(currentVO.getParkDate()) + timeOutHms));
//                            orderChargeTimeSectionVO.setPolicyId(currentVO.getPolicyId());
//                            orderChargeTimeSectionVO.setIsFree(currentVO.getIsFree());
//                            orderChargeTimeSectionList.add(orderChargeTimeSectionVO);
//                        }
//                    }
//                }
//            }
//        } else {
//            return null;
//        }
//        return orderChargeTimeSectionList;
//    }


//    private List<OrderChargeTimeVO> getOrderChargeTimeVOList(Date timeIn, Date timeOut, String parkCode, String plateNo, String plateType) throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
//
//        List<OrderChargeTimeVO> orderChargeTimeList = new ArrayList<>();
//
//        Calendar startDate = Calendar.getInstance();
//        Calendar endDate = Calendar.getInstance();
//
//
//        startDate.setTime(sdf.parse(sdf2.format(timeIn) + " 00:00:00"));
//        endDate.setTime(timeOut);
//
//
//        if (startDate.compareTo(endDate) > 0) {
//            return null;
//        }
//
//
//        Calendar currentDate = startDate;
//
//        do {
//            OrderChargeTimeVO orderChargeTimeVO = new OrderChargeTimeVO();
//            Boolean isFreeFlag = Boolean.valueOf(false);
//
//
//            List<ChargePlanConfig> configList = new ArrayList<>();
//            configList = this.chargePlanConfigMapper.selectByDateParkPlate(sdf.format(currentDate.getTime()), parkCode, plateType);
//
//
//            int leaguerMonthlyAndWhiteCount = 0;
//            leaguerMonthlyAndWhiteCount = this.orderInfoMapper.queryCountLeaguerMonthlyAndWhite(parkCode, plateNo, plateType, DateUtil.formatDateYMDHMS(currentDate.getTime()));
//            if (leaguerMonthlyAndWhiteCount > 0) {
//                isFreeFlag = Boolean.valueOf(true);
//            } else {
//                isFreeFlag = Boolean.valueOf(false);
//            }
//
//            if (configList != null && configList.size() > 0) {
//                orderChargeTimeVO.setParkDate(sdf2.parse(sdf2.format(currentDate.getTime())));
//                orderChargeTimeVO.setPolicyId(((ChargePlanConfig) configList.get(0)).getPolicyId());
//                orderChargeTimeVO.setIsFree(isFreeFlag);
//                orderChargeTimeList.add(orderChargeTimeVO);
//            } else {
//
//                orderChargeTimeVO.setParkDate(sdf2.parse(sdf2.format(currentDate.getTime())));
//                orderChargeTimeVO.setPolicyId("");
//                orderChargeTimeVO.setIsFree(isFreeFlag);
//                orderChargeTimeList.add(orderChargeTimeVO);
//            }
//
//
//            currentDate.add(5, 1);
//        } while (currentDate.compareTo(endDate) <= 0);
//
//
//        return orderChargeTimeList;
//    }
//
//
//    private Map<String, Double> getSubChargeMap(String policyId, Date timeIn, Date timeOut, String plateNo, String plateType, String parkCode) throws Exception {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            SimpleDateFormat sdf3 = new SimpleDateFormat(" HH:mm:ss");
//
//            Map<String, Double> map = new HashMap<>();
//
//            map.put("subTotal", Double.valueOf(0.0D));
//            map.put("subPayFee", Double.valueOf(0.0D));
//
//            double calculateOrderFee = 0.0D;
//            calculateOrderFee = executeOrderFee(policyId, timeIn, timeOut).doubleValue();
//            map.put("subTotal", Double.valueOf(calculateOrderFee));
//
//
//            Date beginDate = DateUtil.parseDateYMD(DateUtil.formatDateYMD(timeIn));
//            Date endDate = DateUtil.parseDateYMD(DateUtil.formatDateYMD(timeOut));
//            List<Date> dateList = DateUtils.getDateFromRange(beginDate, endDate);
//
//            if (dateList.size() == 1) {
//
//
//                int leaguerMonthlyAndWhiteCount = 0;
//                leaguerMonthlyAndWhiteCount = this.orderInfoMapper.queryCountLeaguerMonthlyAndWhite(parkCode, plateNo, plateType, DateUtil.formatDateYMDHMS(timeOut));
//                if (leaguerMonthlyAndWhiteCount > 0) {
//                    map.put("subPayFee", Double.valueOf(0.0D));
//                    return map;
//                }
//
//                String timeInStr = DateUtil.formatDateYMDHMS(timeIn);
//                String timeOutStr = DateUtil.formatDateYMDHMS(timeOut);
//                double subMontylyChargeFeeTotal = 0.0D;
//                double eachSubMonthlyChargeFeeSum = 0.0D;
//                MonthlyPlateInfo monthlyPlateInfoVO = this.monthlyPlateInfoMapper.queryMonthlyPlateInfo4Charge(plateNo, plateType, parkCode, timeInStr);
//                if (monthlyPlateInfoVO != null) {
//
//                    List<SubMonthlyChargeTimeSectionVO> subMonthlyChargeTimeSectionList = new ArrayList<>();
//                    subMonthlyChargeTimeSectionList = getSubMonthlyChargeTimeSectionList(DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr), monthlyPlateInfoVO);
//                    for (int i = 0; i < subMonthlyChargeTimeSectionList.size(); i++) {
//                        eachSubMonthlyChargeFeeSum = 0.0D;
//                        eachSubMonthlyChargeFeeSum = executeOrderFee(policyId, ((SubMonthlyChargeTimeSectionVO) subMonthlyChargeTimeSectionList.get(i)).getBeginTime(), ((SubMonthlyChargeTimeSectionVO) subMonthlyChargeTimeSectionList.get(i)).getEndTime()).doubleValue();
//                        subMontylyChargeFeeTotal += eachSubMonthlyChargeFeeSum;
//                    }
//                } else {
//
//                    subMontylyChargeFeeTotal = executeOrderFee(policyId, DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr)).doubleValue();
//                }
//
//                map.put("subPayFee", Double.valueOf((subMontylyChargeFeeTotal > calculateOrderFee) ? calculateOrderFee : subMontylyChargeFeeTotal));
//                return map;
//            }
//            if (dateList.size() > 0) {
//
//                double subMontylyChargeFeeTotal = 0.0D;
//                for (int i = 0; i < dateList.size(); i++) {
//
//                    double eachSubMonthlyChargeFeeSum = 0.0D;
//
//                    int leaguerMonthlyAndWhiteCount = 0;
//                    leaguerMonthlyAndWhiteCount = this.orderInfoMapper.queryCountLeaguerMonthlyAndWhite(parkCode, plateNo, plateType, DateUtil.formatDateYMDHMS(dateList.get(i)));
//                    if (leaguerMonthlyAndWhiteCount > 0) {
//
//                        eachSubMonthlyChargeFeeSum = 0.0D;
//                    } else {
//
//                        MonthlyPlateInfo monthlyPlateInfoVO = this.monthlyPlateInfoMapper.queryMonthlyPlateInfo4Charge(plateNo, plateType, parkCode, DateUtil.formatDateYMDHMS(dateList.get(i)));
//                        if (monthlyPlateInfoVO != null) {
//
//                            List<SubMonthlyChargeTimeSectionVO> subMonthlyChargeTimeSectionList = new ArrayList<>();
//                            if (i == 0) {
//
//                                String timeInStr = sdf.format(dateList.get(i)) + sdf3.format(timeIn);
//                                String timeOutStr = sdf.format(dateList.get(i)) + " 23:59:59";
//                                subMonthlyChargeTimeSectionList = getSubMonthlyChargeTimeSectionList(DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr), monthlyPlateInfoVO);
//                            } else if (i > 0 && i < dateList.size() - 1) {
//
//                                String timeInStr = sdf.format(dateList.get(i)) + " 00:00:00";
//                                String timeOutStr = sdf.format(dateList.get(i)) + " 23:59:59";
//                                subMonthlyChargeTimeSectionList = getSubMonthlyChargeTimeSectionList(DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr), monthlyPlateInfoVO);
//                            } else if (i == dateList.size() - 1) {
//
//                                String timeInStr = sdf.format(dateList.get(i)) + " 00:00:00";
//                                String timeOutStr = sdf.format(dateList.get(i)) + sdf3.format(timeOut);
//                                subMonthlyChargeTimeSectionList = getSubMonthlyChargeTimeSectionList(DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr), monthlyPlateInfoVO);
//                            }
//
//                            for (int j = 0; j < subMonthlyChargeTimeSectionList.size(); j++) {
//                                eachSubMonthlyChargeFeeSum += executeOrderFee(policyId, ((SubMonthlyChargeTimeSectionVO) subMonthlyChargeTimeSectionList.get(j)).getBeginTime(), ((SubMonthlyChargeTimeSectionVO) subMonthlyChargeTimeSectionList.get(j)).getEndTime()).doubleValue();
//
//                            }
//                        } else if (i == 0) {
//
//                            String timeInStr = sdf.format(dateList.get(i)) + sdf3.format(timeIn);
//                            String timeOutStr = sdf.format(dateList.get(i)) + " 23:59:59";
//                            eachSubMonthlyChargeFeeSum = executeOrderFee(policyId, DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr)).doubleValue();
//                        } else if (i > 0 && i < dateList.size() - 1) {
//
//                            String timeInStr = sdf.format(dateList.get(i)) + " 00:00:00";
//                            String timeOutStr = sdf.format(dateList.get(i)) + " 23:59:59";
//                            eachSubMonthlyChargeFeeSum = executeOrderFee(policyId, DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr)).doubleValue();
//                        } else if (i == dateList.size() - 1) {
//
//                            String timeInStr = sdf.format(dateList.get(i)) + " 00:00:00";
//                            String timeOutStr = sdf.format(dateList.get(i)) + sdf3.format(timeOut);
//                            eachSubMonthlyChargeFeeSum = executeOrderFee(policyId, DateUtil.parseDateYMDHMS(timeInStr), DateUtil.parseDateYMDHMS(timeOutStr)).doubleValue();
//                        }
//                    }
//
//
//                    subMontylyChargeFeeTotal += eachSubMonthlyChargeFeeSum;
//                }
//
//                map.put("subPayFee", Double.valueOf((subMontylyChargeFeeTotal > calculateOrderFee) ? calculateOrderFee : subMontylyChargeFeeTotal));
//                return map;
//            }
//
//            map.put("subPayFee", Double.valueOf(calculateOrderFee));
//            return map;
//        } catch (Exception e) {
//            this.log.info("获取分时包月计费时段，发生异常。Exception=" + e);
//            throw e;
//        }
//    }
//
//
//    private List<SubMonthlyChargeTimeSectionVO> getSubMonthlyChargeTimeSectionList(Date orderSameDayBeginTime, Date orderSameDayEndTime, MonthlyPlateInfo monthlyPlateInfoVO) throws Exception {
//        try {
//            String flag;
//            List<SubMonthlyChargeTimeSectionVO> subMonthlyChargeTimeSectionList = new ArrayList<>();
//
//            String monthlyType = monthlyPlateInfoVO.getMonthlyType();
//            int beginHour = Integer.parseInt(monthlyPlateInfoVO.getBeginHour());
//            int beginMinute = Integer.parseInt(monthlyPlateInfoVO.getBeginMinute());
//            int endHour = Integer.parseInt(monthlyPlateInfoVO.getEndHour());
//            int endMinute = Integer.parseInt(monthlyPlateInfoVO.getEndMinute());
//
//
//            if (beginHour < endHour) {
//                flag = "oneSection";
//            } else if (beginHour == endHour) {
//                if (beginMinute < endMinute) {
//                    flag = "oneSection";
//                } else {
//                    flag = "twoSection";
//                }
//            } else {
//                flag = "twoSection";
//            }
//
//            if (StringUtil.equals(flag, "oneSection")) {
//
//                String ymd = DateUtil.formatDateYMD(orderSameDayBeginTime);
//                String dayMonthlyBeginTimeStr = ymd + " " + monthlyPlateInfoVO.getBeginHour() + ":" + monthlyPlateInfoVO.getBeginMinute() + ":00";
//                String dayMonthlyEndTimeStr = ymd + " " + monthlyPlateInfoVO.getEndHour() + ":" + monthlyPlateInfoVO.getEndMinute() + ":59";
//                Date dayMonthlyBeginTime = DateUtil.parseDateYMDHMS(dayMonthlyBeginTimeStr);
//                Date dayMonthlyEndTime = DateUtil.parseDateYMDHMS(dayMonthlyEndTimeStr);
//                return getSameDayMonthlyChargeTimeSectionList(orderSameDayBeginTime, orderSameDayEndTime, dayMonthlyBeginTime, dayMonthlyEndTime);
//            }
//            if (StringUtil.equals(flag, "twoSection")) {
//
//                String ymd = DateUtil.formatDateYMD(orderSameDayBeginTime);
//                String nightMonthlyBeginTimeStr1 = ymd + " 00:00:00";
//                String nightMonthlyEndTimeStr1 = ymd + " " + monthlyPlateInfoVO.getEndHour() + ":" + monthlyPlateInfoVO.getEndMinute() + ":59";
//                String nightMonthlyBeginTimeStr2 = ymd + " " + monthlyPlateInfoVO.getBeginHour() + ":" + monthlyPlateInfoVO.getBeginMinute() + ":00";
//                String nightMonthlyEndTimeStr2 = ymd + " 23:59:59";
//                Date nightMonthlyBeginTime1 = DateUtil.parseDateYMDHMS(nightMonthlyBeginTimeStr1);
//                Date nightMonthlyEndTime1 = DateUtil.parseDateYMDHMS(nightMonthlyEndTimeStr1);
//                Date nightMonthlyBeginTime2 = DateUtil.parseDateYMDHMS(nightMonthlyBeginTimeStr2);
//                Date nightMonthlyEndTime2 = DateUtil.parseDateYMDHMS(nightMonthlyEndTimeStr2);
//                return getCrossDayMonthlyChargeTimeSectionList(orderSameDayBeginTime, orderSameDayEndTime, nightMonthlyBeginTime1, nightMonthlyEndTime1, nightMonthlyBeginTime2, nightMonthlyEndTime2);
//            }
//            return null;
//        } catch (Exception e) {
//            this.log.info("获取分时包月计费时段，发生异常。Exception=" + e);
//            throw e;
//        }
//    }
//
//
//    private List<SubMonthlyChargeTimeSectionVO> getSameDayMonthlyChargeTimeSectionList(Date orderBeginTime, Date orderEndTime, Date dayMonthlyBeginTime, Date dayMonthlyEndTime) {
//        try {
//            List<SubMonthlyChargeTimeSectionVO> list = new ArrayList<>();
//
//
//            if (orderEndTime.getTime() <= dayMonthlyBeginTime.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(orderBeginTime);
//                sectionVO.setEndTime(orderEndTime);
//                list.add(sectionVO);
//                return list;
//            }
//
//            if (orderBeginTime.getTime() >= dayMonthlyEndTime.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(orderBeginTime);
//                sectionVO.setEndTime(orderEndTime);
//                list.add(sectionVO);
//                return list;
//            }
//
//            if (orderBeginTime.getTime() >= dayMonthlyBeginTime.getTime() && orderBeginTime
//                    .getTime() <= dayMonthlyEndTime.getTime() && orderEndTime
//                    .getTime() > dayMonthlyEndTime.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(dayMonthlyEndTime);
//                sectionVO.setEndTime(orderEndTime);
//                list.add(sectionVO);
//                return list;
//            }
//
//
//            if (orderEndTime.getTime() >= dayMonthlyBeginTime.getTime() && orderEndTime
//                    .getTime() <= dayMonthlyEndTime.getTime() && orderBeginTime
//                    .getTime() < dayMonthlyBeginTime.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(orderBeginTime);
//                sectionVO.setEndTime(dayMonthlyBeginTime);
//                list.add(sectionVO);
//                return list;
//            }
//
//
//            if (orderBeginTime.getTime() < dayMonthlyBeginTime.getTime() && orderEndTime
//                    .getTime() > dayMonthlyEndTime.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO1 = new SubMonthlyChargeTimeSectionVO();
//                SubMonthlyChargeTimeSectionVO sectionVO2 = new SubMonthlyChargeTimeSectionVO();
//
//                sectionVO1.setBeginTime(orderBeginTime);
//                sectionVO1.setEndTime(dayMonthlyBeginTime);
//                list.add(sectionVO1);
//
//                sectionVO2.setBeginTime(dayMonthlyEndTime);
//                sectionVO2.setEndTime(orderEndTime);
//                list.add(sectionVO2);
//                return list;
//            }
//
//
//            if (orderBeginTime.getTime() < dayMonthlyBeginTime.getTime() || orderEndTime
//                    .getTime() <= dayMonthlyEndTime.getTime()) ;
//
//
//            return list;
//        } catch (Exception e) {
//            this.log.info("获取白天分时包月计费时段，发生异常。Exception=" + e);
//            throw e;
//        }
//    }
//
//
//    private List<SubMonthlyChargeTimeSectionVO> getCrossDayMonthlyChargeTimeSectionList(Date orderBeginTime, Date orderEndTime, Date nightMonthlyBeginTime1, Date nightMonthlyEndTime1, Date nightMonthlyBeginTime2, Date nightMonthlyEndTime2) {
//        try {
//            List<SubMonthlyChargeTimeSectionVO> list = new ArrayList<>();
//
//            if (orderBeginTime.getTime() >= nightMonthlyEndTime1.getTime() && orderEndTime
//                    .getTime() <= nightMonthlyBeginTime2.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(orderBeginTime);
//                sectionVO.setEndTime(orderEndTime);
//                list.add(sectionVO);
//                return list;
//            }
//
//            if (orderBeginTime.getTime() >= nightMonthlyBeginTime1.getTime() && orderBeginTime.getTime() <= nightMonthlyEndTime1.getTime() && orderEndTime
//                    .getTime() >= nightMonthlyBeginTime2.getTime() && orderEndTime.getTime() <= nightMonthlyEndTime2.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(nightMonthlyEndTime1);
//                sectionVO.setEndTime(nightMonthlyBeginTime2);
//                list.add(sectionVO);
//                return list;
//            }
//
//            if (orderBeginTime.getTime() >= nightMonthlyBeginTime1.getTime() && orderBeginTime.getTime() <= nightMonthlyEndTime1.getTime() && orderEndTime
//                    .getTime() <= nightMonthlyBeginTime2.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(nightMonthlyEndTime1);
//                sectionVO.setEndTime(orderEndTime);
//                list.add(sectionVO);
//                return list;
//            }
//
//            if (orderBeginTime.getTime() >= nightMonthlyEndTime1.getTime() && orderEndTime
//                    .getTime() >= nightMonthlyBeginTime2.getTime() && orderEndTime.getTime() <= nightMonthlyEndTime2.getTime()) {
//                SubMonthlyChargeTimeSectionVO sectionVO = new SubMonthlyChargeTimeSectionVO();
//                sectionVO.setBeginTime(orderBeginTime);
//                sectionVO.setEndTime(nightMonthlyBeginTime2);
//                list.add(sectionVO);
//                return list;
//            }
//
//
//            if (orderBeginTime.getTime() < nightMonthlyBeginTime1.getTime() || orderBeginTime.getTime() > nightMonthlyEndTime1.getTime() || orderEndTime
//                    .getTime() < nightMonthlyBeginTime1.getTime() || orderEndTime.getTime() <= nightMonthlyEndTime1.getTime())
//                ;
//
//
//            if (orderBeginTime.getTime() < nightMonthlyBeginTime2.getTime() || orderBeginTime.getTime() > nightMonthlyEndTime2.getTime() || orderEndTime
//                    .getTime() < nightMonthlyBeginTime2.getTime() || orderEndTime.getTime() <= nightMonthlyEndTime2.getTime())
//                ;
//
//
//            return list;
//        } catch (Exception e) {
//            this.log.info("获取夜间分时包月计费时段，发生异常。Exception=" + e);
//            throw e;
//        }
//    }
//
//
//    private Double executeOrderFee(String policyId, Date timeIn, Date timeOut) {
//        Double orderFee = Double.valueOf(0.0D);
//        try {
//            if (StringUtil.isNotEmpty(policyId)) {
//                Rule rule = getRuleByPolicyId(policyId);
//                ParkingInfo parkingInfo = new ParkingInfo(timeIn, timeOut);
//                ParkingAlgorithm algorithm = new ParkingAlgorithm(parkingInfo, rule);
//                algorithm.executeCalculate();
//                orderFee = Double.valueOf(algorithm.getParkingInfo().getCost());
//            }
//        } catch (Exception e) {
//            this.log.error("计费过程产生错误：" + e.getMessage());
//            this.log.error("抛出异常", e);
//        }
//        return orderFee;
//    }
//
//
//    public String queryReferenceCost(String parkCode, String plateType) {
//        return this.chargePlanConfigMapper.queryReferenceCost(parkCode, plateType);
//    }
//
//
//    public Map<String, String> getOrderFeeWithTimeOut(String orderNo, Date timeOut) throws Exception {
//        RecOrder order = this.recOrderMapper.selectByPrimaryKey(orderNo);
//
//        String sysConfigByKey = CommonCacheData.getSysConfigByKey("special_free_license_plate_type");
//        if (sysConfigByKey != null && order.getPlateType() != null && sysConfigByKey.indexOf(order.getPlateType()) != -1) {
//            Map<String, String> map = new HashMap<>();
//            map.put("consAll", "0");
//            map.put("payFee", "0");
//            return map;
//        }
//
//        if (timeOut == null) {
//            String timeOutStr = this.createOrderByDevServiceImpl.queryOrderEndTimeStr(order);
//            timeOut = DateUtil.toDateTime(timeOutStr);
//        }
//
//        Date beginTime = order.getTimeIn();
//        Date endTime = timeOut;
//        String parkCode = order.getParkCode();
//        String plateNo = order.getPlateNo();
//        String plateType = "7201";
//        if (StringUtil.isNotEmpty(order.getPlateType())) {
//            plateType = order.getPlateType();
//        }
//        if (StringUtils.contains(plateNo, "学")) {
//            plateType = "7201";
//        }
//
//
//        Jedis jedis = RedisUtil.getJedis();
//        String key = RedisKeyUtil.getBizKey(RedisValue.String, "paymentFreeCache", orderNo);
//        String payTimeStr = jedis.get(key);
//        jedis.close();
//        if (payTimeStr != null) {
//            this.log.info("有预支付记录，将使用支付完成时间计费：" + payTimeStr);
//            endTime = DateUtils.parseDate(payTimeStr, null);
//        } else {
//            this.log.info("无预支付记录或已超时，将使用驶离时间计费：" + endTime);
//        }
//        return getCalcParkFee(beginTime, endTime, plateNo, plateType, parkCode);
//    }
//
//
//    public OrderFeeCaclResult getOrderFeeWithoutTimeOut(String orderNo) throws Exception {
//        OrderFeeCaclResult calcResult = new OrderFeeCaclResult();
//        try {
//            RecOrder order = this.recOrderMapper.selectByPrimaryKey(orderNo);
//            Map<String, String> orderFeeMap = new HashMap<>();
//
//            if ("1101".equals(order.getOrderState())) {
//
//                String timeOutStr = this.createOrderByDevServiceImpl.queryOrderEndTimeStr(order);
//                calcResult.setTimeOut(DateUtil.toDateTime(timeOutStr));
//
//
//                orderFeeMap = getOrderFeeWithTimeOut(orderNo, DateUtil.toDateTime(timeOutStr));
//                calcResult.setConsAll(Double.valueOf(orderFeeMap.get("consAll")));
//                calcResult.setRealCost(Double.valueOf(orderFeeMap.get("payFee")));
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                double parkInv = 0.0D;
//
//                Date timeIn = order.getTimeIn();
//                if ((int) (timeIn.getTime() % 60000L / 1000L) != 0) {
//                    timeIn = new Date(timeIn.getTime() + 60000L);
//                }
//
//                timeIn = sdf.parse(DateUtil.toDate("yyyy-MM-dd HH:mm:00", timeIn));
//                Date timeLeave = sdf.parse(DateUtil.toDate("yyyy-MM-dd HH:mm:00", calcResult.getTimeOut()));
//                parkInv = (timeLeave.getTime() - timeIn.getTime()) / 1000.0D / 60.0D;
//                if (parkInv < 0.0D) {
//                    parkInv = 0.0D;
//                }
//                calcResult.setParkInv((int) parkInv);
//            } else {
//                calcResult.setConsAll(order.getConsAll());
//                calcResult.setRealCost(order.getRealCost());
//                calcResult.setParkInv(order.getParkInv().intValue());
//                calcResult.setTimeOut(order.getTimeOut());
//            }
//            return calcResult;
//        } catch (Exception e) {
//            this.log.error("抛出异常", e);
//            return null;
//        }
//    }
}

