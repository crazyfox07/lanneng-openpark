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
@Table(name = "log_del_inner_cars")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class LogDelInnerCars implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String carno;
    private String orderNo;
    private String regionCode;
    private Date logTime;
    private String userId;
    private String userName;
}
