package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ParkVipInfoParams {
    private String carno;
    private String carnoType;
    private String name;
    private String sex;
    private int age;
    private String licenceType;
    private String licenceNo;
    private String phone;
    private String carcolor;
    private String brand;
    private String brandSubType;
    private String vipType;
    private double vipDiscount;
    private String validBegintime;
    private String expiryDate;
    private double remainMoney;
    private String validScope;
    private String deleteStatus;
    private String freezeStatus;
    private String freezeUntil;
    private int valid;
    private String cloudMember;
    private String groupNid;
}
