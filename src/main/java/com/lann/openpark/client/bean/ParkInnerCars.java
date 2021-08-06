package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ParkInnerCars {
    private String nid;
    private String carno;
    private String inTime;
    private String devicecode;
    private String inImage;
    private String devicename;
    private String pointcode;
    private String pointname;
    private String regionCode;
    private String regionName;

}
