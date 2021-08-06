package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "black_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class BlackInfo {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String blackId;
    private String carno;
    private String carnoType;
    private Date validBegintime;
    private Date expiryDate;
}
