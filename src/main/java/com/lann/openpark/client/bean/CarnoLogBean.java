package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CarnoLogBean {
    private String id;
    private String carno;
    private String userId;
    private String userName;
    private String optTime;
    private String deviceCode;
    private String deviceName;
    private String imgPath;
}
