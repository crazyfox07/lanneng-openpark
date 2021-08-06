package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DetectInfo {
    private String nid;
    private String carno;
    private String driveTime;
    private String direction;
    private String pointname;
    private String pointcode;
    private String pic;
    private String regionCode;
    private String regionName;
}
