package com.lann.openpark.client.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EquipGateBean {
    private String devicecode;
    private String devicename;
    private String pointcode;
    private String pointname;
    private String ip;
    private String videofunc;
    private Integer voiceChannel;
}
