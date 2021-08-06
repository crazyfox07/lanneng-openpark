package com.lann.openpark.charge.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "charge2_policy")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Charge2Policy {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String policyName;
    private String plateType;
    private String referCost;
    private String configTemplate;
    private String description;
}
