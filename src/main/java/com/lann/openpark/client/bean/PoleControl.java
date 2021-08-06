package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PoleControl {
    private String logTime;
    private String deviceCode;
    private String deviceName;
    private String userId;
    private String userName;
    private String regionCode;
    private String regionName;
    private String imgPath;
}
