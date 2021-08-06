package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ParkIncomeBean {
    private String payType;
    private String cateName;
    private double fee;
}
