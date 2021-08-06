package com.lann.openpark.charge.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "charge_plan")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ChargePlan {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String planName;
    private Date beginDate;
    private Date endDate;
    private String planPriorityType;
    private Date updateTime;
    private String carTypes;
    private String regions;
    private String chargePolicyId;
    private double policyPriority;
}
