package com.lann.openpark.charge.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "charge_plan_config")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ChargePlanConfig {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String parkId;
    private String planId;
    private String plateType;
    private String policyId;
    private String isEffective;
    private Date updateTime;
    private int priority;
}
