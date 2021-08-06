package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "abnormal_out")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class AbnormalOut {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;// id号
    private String plateNo;// 车牌号
    private String plateType;// 车牌类型
    private int cumulNum;// 累计次数
}
