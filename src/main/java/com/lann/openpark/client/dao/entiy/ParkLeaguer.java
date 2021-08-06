package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "park_leaguer")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkLeaguer {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String leaguerId;
    private String leaguerPhone;
    private String leaguerName;
    private Date startDate;
    private Date endDate;
}
