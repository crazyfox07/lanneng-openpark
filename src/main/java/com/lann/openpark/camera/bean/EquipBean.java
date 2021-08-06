package com.lann.openpark.camera.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 设备信息
 * Created by songqiang on 2020/7/1.
 */
@Data
@ToString
public class EquipBean implements Serializable {
    private String devicecode;
    private String devicename;
    private String pointcode;
    private String ip;
    private int port;
    private String provider;
    private String videofunc;
    private String runMode;
    private String bindIp;
    private String validDay;
    private int downloadstatus;
    private int keepCloseOrCancle;
    private int firstSecond;
    private int voiceChannel;
}
