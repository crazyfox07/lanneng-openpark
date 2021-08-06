package com.lann.openpark.charge.bizobj.config;


import com.lann.openpark.charge.dict.Const;

public class LoopChargeConfig {
    //循环类型  自定义 or 24个小时，Constants内有配置，方便转换为枚举
    private String loopType = Const.LOOP_CUSTOM;
    ;
    //定义时间点，默认为0点
    private int customPoint = 0;

    public String getLoopType() {
        return this.loopType;
    }


    public void setLoopType(String loopType) {
        this.loopType = loopType;
    }


    public int getCustomPoint() {
        return this.customPoint;
    }


    public void setCustomPoint(int customPoint) {
        this.customPoint = customPoint;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("**********循环单位配置规则************\n");
        sb.append("[循环类型]=").append(this.loopType).append("\n");
        sb.append("[自定义点]=").append(this.customPoint).append("\n");
        return sb.toString();
    }
}

