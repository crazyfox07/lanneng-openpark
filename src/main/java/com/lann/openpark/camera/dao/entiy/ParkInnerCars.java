package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name="park_inner_cars")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkInnerCars {

    @Id
    private String nid;// id号
    private String carno;// 车牌号
    private String parkingType;// 停车类型(1：白名单车辆、2：储值车辆、3：包月车辆、4：临时会员车辆；
                                // 5：云端会员车辆；6：共享预约；7：共享包月；8、出租车9：临时车 0：特殊车辆)
    private String groupNid;//
    private String regionCode;
    private String vipOutnumber;//

    private String plateType;// 车牌类型



}
