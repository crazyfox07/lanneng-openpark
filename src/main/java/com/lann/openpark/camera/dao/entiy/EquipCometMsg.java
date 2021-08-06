package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name="equip_comet_msg")
public class EquipCometMsg {
    @Id
    private String devicecode;
    private String devicename;
    private String pointcode;
    private String ip;
    private int port;
    private Date cometTime;
}
