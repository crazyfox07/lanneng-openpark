package com.lann.openpark.camera.bean;

import lombok.Data;
import lombok.ToString;

/**
 * Created by songqiang on 2020/7/1.
 */
@Data
@ToString
public class EquipGateInfo {
    private String devicecode;// 设备ID
    private String devicename;// 设备名称
    private String pointcode;// 出入口ID
    private String ip;// 设备IP
    private int port;// 设备port
    private String pointname;// 出入口名称
    private String parkcode;// 停车场编号
    private String regionCode;// 区域编号
    private String videofunc;// 出入口标识 9 入口 10 出口
    private int voiceChannel;// 声音输出通道
    private String pointFunc;// 通行方式

    private String regionType;// 区域类型
    private String parentRegion;// 上级区域
    private String restrictedAccess;// 车位满限制进入
    private String whitelistPrivileges;// 白名单特权
}
