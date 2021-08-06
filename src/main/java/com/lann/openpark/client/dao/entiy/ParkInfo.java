package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 停车场信息
 *
 * @Author songqiang
 * @Description
 * @Date 2020/10/29 14:19
 **/
@Data
@ToString
@Entity
@Table(name = "park_info")
public class ParkInfo implements Serializable {
    @Id
    private String parkcode;
    private String parkname;
    private String roadid;
    private String address;
    private String location;
    private String principal;
    private String phone;
    private int berthCount;
    private int remainBerthCount;
    private int rectifyCount;
    private int downloadstatus;
    private int firstSecond;
    private int keepCloseOrCancle;
    private int port;
    private int voiceChannel;


}
