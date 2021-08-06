package com.lann.openpark.camera.bean;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class ParkVipBean {
    private String vipId;
    private String carno;
    private String carnoType;
    private String name;
    private String phone;
    private String vipType;// 会员类型
    private Double vipDiscount;// 会员折扣率
    private Date validBegintime;// 生效时间
    private Date expiryDate;// 有效期止
    private Float remainMoney;// 余额
    private String groupNid;// vip组编号
}
