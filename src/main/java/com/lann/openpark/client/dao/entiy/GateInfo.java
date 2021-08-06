package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

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
@Table(name = "gate_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class GateInfo implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 12)
    private String pointcode;
    private String pointname;
    private String pointFunc;
    private String parkcode;
    private String position;
    private String regionCode;

    // mod by sq 20210802
    private String geteType; // 出口类型
}
