package com.lann.openpark.charge.bizobj;


import java.util.List;

import com.lann.openpark.charge.bizobj.config.*;
import org.joda.time.LocalTime;


public class Rule {
    private List<LocalTime[]> freeTimeSections = null;


    private String calcType = "A0109";
    private int[] dayInterval = null;

    private ChargeRuleUnit allDayChargeConfig;

    private ChargeRuleUnit dayChargeConfig;

    private ChargeRuleUnit nightChargeConfig;

    private FreeTimeConfig freeTimeConfig = null;


    private LoopChargeConfig loopConfig = null;


    private BorrowTimeConfig borrowTimeConfig = new BorrowTimeConfig();


    private double limitCharge = 0.0D;


    private AttachRule attachRule = null;


    public void setDayInterval(int[] dayInterval) throws Exception {
        if (dayInterval[0] == dayInterval[1]) {
            throw new RuntimeException("Error interval");
        }
        this.dayInterval = dayInterval;
        this.calcType = "A0110";
    }


    public void setCalcType(String calcType) {
        this.calcType = calcType;
        if (calcType.equals("A0109")) {
            this.dayInterval = null;
        }
    }


    public ChargeRuleUnit getAllDayChargeConfig() {
        return this.allDayChargeConfig;
    }


    public void setAllDayChargeConfig(ChargeRuleUnit allDayChargeConfig) {
        this.allDayChargeConfig = allDayChargeConfig;
    }


    public ChargeRuleUnit getDayChargeConfig() {
        return this.dayChargeConfig;
    }


    public void setDayChargeConfig(ChargeRuleUnit dayChargeConfig) {
        this.dayChargeConfig = dayChargeConfig;
    }


    public ChargeRuleUnit getNightChargeConfig() {
        return this.nightChargeConfig;
    }


    public void setNightChargeConfig(ChargeRuleUnit nightChargeConfig) {
        this.nightChargeConfig = nightChargeConfig;
    }


    public String getCalcType() {
        return this.calcType;
    }


    public int[] getDayInterval() {
        return this.dayInterval;
    }


    public FreeTimeConfig getFreeTimeConfig() {
        return this.freeTimeConfig;
    }


    public void setFreeTimeConfig(FreeTimeConfig freeTimeConfig) {
        this.freeTimeConfig = freeTimeConfig;
    }


    public List<LocalTime[]> getFreeTimeSections() {
        return this.freeTimeSections;
    }


    public void setFreeTimeSections(List<LocalTime[]> freeTimeSections) {
        this.freeTimeSections = freeTimeSections;
    }


    public LoopChargeConfig getLoopConfig() {
        return this.loopConfig;
    }


    public void setLoopConfig(LoopChargeConfig loopConfig) {
        this.loopConfig = loopConfig;
    }


    public double getLimitCharge() {
        return this.limitCharge;
    }


    public void setLimitCharge(double limitCharge) {
        this.limitCharge = limitCharge;
    }


    public BorrowTimeConfig getBorrowTimeConfig() {
        return this.borrowTimeConfig;
    }


    public void setBorrowTimeConfig(BorrowTimeConfig borrowTimeConfig) {
        this.borrowTimeConfig = borrowTimeConfig;
    }


    public AttachRule getAttachRule() {
        return this.attachRule;
    }


    public void setAttachRule(AttachRule attachRule) {
        this.attachRule = attachRule;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.freeTimeConfig.toString());


        sb.append("**********????????????????????????************\n");
        if (this.freeTimeSections != null) {
            for (int i = 0; i < this.freeTimeSections.size(); i++) {
                LocalTime[] localTimeArray = this.freeTimeSections.get(i);
                sb.append("[????????????]").append(i + 1).append(": ").append(localTimeArray[0]).append("~")
                        .append(localTimeArray[1]).append("\n");
            }
        }

        sb.append(this.loopConfig.toString());


        sb.append("**********?????????????????????************\n");
        sb.append("[?????????]=" + this.limitCharge + "\n");


        sb.append("**********????????????????????????************\n");
        if (this.dayInterval == null) {
            sb.append("????????????????????????").append("\n");
        } else {
            sb.append("???????????????").append(this.dayInterval[0] / 60).append(":").append(this.dayInterval[0] % 60).append("~")
                    .append(this.dayInterval[1] / 60).append(":").append(this.dayInterval[1] % 60).append("\n");
        }


        if (this.allDayChargeConfig != null) {
            sb.append("**********???????????????????????????************\n");
            sb.append(this.allDayChargeConfig.toString());
        }


        if (this.dayChargeConfig != null) {
            sb.append("**********????????????????????????************\n");
            sb.append(this.dayChargeConfig.toString());
        }

        if (this.nightChargeConfig != null) {
            sb.append("**********????????????????????????************\n");
            sb.append(this.nightChargeConfig.toString());
        }


        sb.append(this.borrowTimeConfig.toString());

        return sb.toString();
    }
}

