package com.lann.openpark.charge.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "charge_plan_region")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ChargePlanRegion {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String regionCode;
    private String planId;
}
