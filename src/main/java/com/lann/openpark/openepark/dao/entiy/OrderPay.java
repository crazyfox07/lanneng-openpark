package com.lann.openpark.openepark.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "order_pay")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class OrderPay {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;// id号
    private String parkCode;// 停车场编号
    private String orderNo;// 订单编号
    private Float payFee;// 支付金额
    private String payType;// 支付方式
    private String outTradeNo;// 支付流水号
    private Float derateFee;// 打折费用
    private Float couponFee;// 优惠券费用
    private Date payTime;// 支付时间
    private Date insertTime;// 插入时间
    private String userId;// 用户ID
}
