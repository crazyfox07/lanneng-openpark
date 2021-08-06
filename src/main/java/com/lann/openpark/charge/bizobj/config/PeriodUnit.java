package com.lann.openpark.charge.bizobj.config;

/**
 * 时段配置单元
 *
 * @Author songqiang
 * @Description
 * @Date 2020/6/24 14:40
 **/
public class PeriodUnit {
    private int start = 0;
    private int end = 0;
    private int timeUnit = 30;
    private double unitCost = 2.0D;


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(this.start / 60).append(":").append(this.start % 60).append("~").append(this.end / 60).append(":").append(this.end % 60)
                .append("-----").append(this.unitCost).append("/").append(this.timeUnit);
        return sb.toString();
    }


    public PeriodUnit() {
    }


    public PeriodUnit(int start, int end, int timeUnit, double unitCost) {
        this.start = start;
        this.end = end;
        this.timeUnit = timeUnit;
        this.unitCost = unitCost;
    }


    public int getStart() {
        return this.start;
    }


    public void setStart(int start) {
        this.start = start;
    }


    public int getEnd() {
        return this.end;
    }


    public void setEnd(int end) {
        this.end = end;
    }


    public int getTimeUnit() {
        return this.timeUnit;
    }


    public void setTimeUnit(int timeUnit) {
        this.timeUnit = timeUnit;
    }


    public double getUnitCost() {
        return this.unitCost;
    }


    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }
}

