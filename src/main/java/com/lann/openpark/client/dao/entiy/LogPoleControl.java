package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 停车场出入口信息
 *
 * @Author songqiang
 * @Description
 * @Date 2020/10/29 14:32
 **/
@Data
@ToString
@Entity
@Table(name = "log_pole_controller")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class LogPoleControl implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 12)
    private String nid;
    private String deviceCode;
    private String deviceName;
    private Date logTime;
    private String userId;
    private String userName;
    private String imgPath;
}
