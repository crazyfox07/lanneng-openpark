package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "sys_car_config")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class SysCarConfig {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;// id号
    private String carno;//
    private String type;//
}
