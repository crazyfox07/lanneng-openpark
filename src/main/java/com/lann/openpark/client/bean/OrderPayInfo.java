package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderPayInfo {
    private String carno;
    private float payFee;
    private String payType;
    private String payTime;
    private Integer duration;
    private String username;
    private String collectiondate1;
    private String collectiondate2;
}
