package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

/**
 * 设备信息
 * Created by songqiang on 2020/7/1.
 */
@Data
@ToString
@Entity
@Table(name = "equip_info")
public class EquipInfo implements Serializable {
    @Id
    private String devicecode;
    private String devicename;
    private String pointcode;
    private String ip;
    private int port;
    private Date buildtime;
    private String provider;
    private String videofunc;
    private String runMode;
    private String bindIp;
    private Time timeBegin;
    private Time timeEnd;
    private String validDay;
    private int downloadstatus;
    private int keepCloseOrCancle;
    private int firstSecond;
    private int voiceChannel;
}
