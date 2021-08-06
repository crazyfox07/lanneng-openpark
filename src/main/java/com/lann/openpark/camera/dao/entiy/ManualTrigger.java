package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name="manual_trigger")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ManualTrigger {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;// id号
    private String orderNo;// 订单编号
    private String plateNo;// 车牌号码
    private String plateType;// 车牌类型
    private String random;// 随机数
    private String devicecode;// 设备ID
}
