package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DelCars {
    private String carno;
    private String logTime;
    private String inTime;
    private String orderNo;
    private String regionCode;
    private String regionName;
    private String userId;
    private String userName;
}
