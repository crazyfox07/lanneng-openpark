package com.lann.openpark.charge.bizobj.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 附加规则配置
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:35
 **/
public class AttachRule {
    int validParkMinutes = 0;
    List<PeriodUnit> periods = new ArrayList<>();
    double attachMoney = 0.0D;


    public void addPeriodUnit(int startMiniteOfDay, int endMiniteOfDay, int timeUnit, double unitCost) {
        if (startMiniteOfDay < 0 || endMiniteOfDay < 0 || timeUnit < 0) {
            throw new RuntimeException();
        }
        PeriodUnit unit = new PeriodUnit(startMiniteOfDay, endMiniteOfDay, timeUnit, unitCost);
        this.periods.add(unit);
    }


    public List<PeriodUnit> getPeriods() {
        return this.periods;
    }


    public double getAttachMoney() {
        return this.attachMoney;
    }


    public void setAttachMoney(double attachMoney) {
        this.attachMoney = attachMoney;
    }


    public int getValidParkMinutes() {
        return this.validParkMinutes;
    }


    public void setValidParkMinutes(int validParkMinutes) {
        this.validParkMinutes = validParkMinutes;
    }


    public void setPeriods(List<PeriodUnit> periods) {
        this.periods = periods;
    }
}

