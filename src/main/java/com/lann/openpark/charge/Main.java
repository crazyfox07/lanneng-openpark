package com.lann.openpark.charge;


import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.bizobj.Rule;
import com.lann.openpark.charge.bizobj.config.*;
import com.lann.openpark.charge.logic.ParkingAlgorithm;
import org.joda.time.LocalTime;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Main {
    public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void main1(String[] args) throws Exception {
        Rule rule = new Rule();


        List<LocalTime[]> freeTimeSections = generFreeSections();
        rule.setFreeTimeSections(null);


        FreeTimeConfig freeTimeConfig = getFreeTimeConfig();
        rule.setFreeTimeConfig(freeTimeConfig);


        rule.setCalcType("A0110");
        int[] daySection = {420, 1260};
        rule.setDayInterval(daySection);

        ChargeRuleUnit dayConfig = new ChargeRuleUnit();
        DurationChargeRule durationRule = new DurationChargeRule();
        durationRule.addDuractionUnit(0, 60, 60, 4.0D);
        durationRule.addDuractionUnit(60, 840, 60, 1.0D);
        dayConfig.setDuractionRule(durationRule);
        rule.setDayChargeConfig(dayConfig);

        ChargeRuleUnit nightConfig = new ChargeRuleUnit();
        PeriodChargeRule nightPeriodRule = new PeriodChargeRule();
        nightPeriodRule.addPeriodUnit(1260, 420, 60, 0.0D);
        nightConfig.setPeriodRule(nightPeriodRule);
        rule.setNightChargeConfig(nightConfig);


        LoopChargeConfig loop = new LoopChargeConfig();
        loop.setLoopType("A0107");
        loop.setCustomPoint(0);
        rule.setLoopConfig(loop);


        BorrowTimeConfig btc = new BorrowTimeConfig();


        btc.setDayTimeCanBeTaken(true);
        btc.setNightTimeCanBeTaken(true);
        btc.setTimeLimit(60);
        rule.setBorrowTimeConfig(btc);


        rule.setLimitCharge(20.0D);


        AttachRule attachRule = new AttachRule();
        attachRule.setValidParkMinutes(60);
        attachRule.addPeriodUnit(420, 480, 60, 1.0D);
        attachRule.addPeriodUnit(480, 540, 60, 1.0D);
        attachRule.addPeriodUnit(1020, 1080, 60, 1.0D);
        attachRule.setAttachMoney(0.0D);


        System.out.println(getCaclFee(rule, "2018-12-24 06:12:11", "2018-12-24 07:05:57"));
    }

    public static double getCaclFee(Rule rule, String in, String out) throws Exception {
        Date d1 = df.parse(in);
        Date d2 = df.parse(out);
        ParkingInfo park = new ParkingInfo(d1, d2);
        ParkingAlgorithm algorithm = new ParkingAlgorithm(park, rule);
        algorithm.executeCalculate();
        return algorithm.getParkingInfo().getCost();
    }


    private static FreeTimeConfig getFreeTimeConfig() {
        FreeTimeConfig freeTimeConfig = new FreeTimeConfig();
        freeTimeConfig.setFreeTime(20);
        freeTimeConfig.setFreeMode("A0106");
        freeTimeConfig.setFreeLimitTime(20);


        freeTimeConfig.setDayAvailable(true);
        freeTimeConfig.setNightAvailable(true);
        return freeTimeConfig;
    }

    private static List<LocalTime[]> generFreeSections() {
        List<LocalTime[]> freeTimeSections = (List) new ArrayList<>();


        return freeTimeSections;
    }

    /**
     * 收费逻辑测试
     *
     * @Author songqiang
     * @Description
     * @Date 2020/6/23 15:09
     **/
    public static void main(String[] args) throws Exception {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateTime timeIn = new DateTime(df.parse("2020-09-08 19:28:00"));
//        System.out.println(timeIn.getMillis());
        
//        DateTime timeOut = new DateTime(df.parse("2020-09-09 7:10:00"));
//        ChargeTest chargeTest = new ChargeTest();
//        Rule rule = chargeTest.getRuleByPolicyId("11111");
//        ParkingInfo parkingInfo = new ParkingInfo(timeIn.toDate(), timeOut.toDate());
//        ParkingAlgorithm algorithm = new ParkingAlgorithm(parkingInfo, rule);
//        algorithm.executeCalculate();
//        Double orderFee = Double.valueOf(algorithm.getParkingInfo().getCost());
//        System.out.println("产生的费用：" + orderFee);\
        InetAddress address = InetAddress.getByName("http://jtss.rzbus.cn/openEpark/");
        System.out.println(address.isReachable(3000));
    }


}


