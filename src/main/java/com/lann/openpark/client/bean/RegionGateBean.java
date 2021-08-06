package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegionGateBean {
    private String pointcode;
    private String pointname;
    private String regionCode;
    private String regionName;
    private String pointFunc;
}
