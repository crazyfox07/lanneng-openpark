package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "park_leaguer_cars")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkLeaguerCars {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;
    private String leaguerId;
    private String plateNo;
    private String plateType;
}
