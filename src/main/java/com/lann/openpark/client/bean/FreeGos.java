package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FreeGos {
    private String orderNo;
    private String carno;
    private String logTime;
    private String inTime;
    private String outTime;
    private String regionName;
    private String inImg;
    private String outImg;
}
