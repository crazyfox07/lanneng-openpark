package com.lann.openpark.park.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ParkInfoBean {
    private String parkcode;
    private String parkname;
    private String roadid;
    private Integer berthCount;// 车位数
    private String address;
    private String location;//
    private String principal;// 负责人
    private String phone;//
    private Integer remainBerthCount;// 车位剩余数
    private Integer rectifyCount;// 车位调整数
}
