package com.lann.openpark.order.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "park_pay_detail")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkPayDetail {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;// id号
    private String carno;
    private String vipType;
    private double charge;
    private double remainMoney;
    private double unitCost;
    private Date validBegintime;
    private Date expiryDate;
    private String isRefund;
    private String operator;
    private Date operTime;


}
