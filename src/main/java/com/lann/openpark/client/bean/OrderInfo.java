package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderInfo {
    private String orderNo;
    private String plateNo;
    private String plateType;
    private String timeIn;
    private String timeOut;
    private String orderStatus;
    private String orderFee;
    private String payFee;
    private String inImg;
    private String outImg;
    private int parkDuration;
}
