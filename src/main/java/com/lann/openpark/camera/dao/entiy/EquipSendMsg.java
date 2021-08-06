package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "equip_send_msg")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class EquipSendMsg implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String devicecode;
    private int msgType;
    private String randomNum;
    private Date insertTime;
    private Integer channel;
    private String content;
    private Integer dataLength;
}
