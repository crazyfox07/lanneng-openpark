package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "carno_log_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class CarnoLogInfo {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    private String carno;
    private String userId;
    private String userName;
    private Date optTime;
    private String deviceCode;
    private String deviceName;
    private String imgPath;
}
