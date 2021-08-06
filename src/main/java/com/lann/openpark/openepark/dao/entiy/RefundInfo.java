package com.lann.openpark.openepark.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name="refund_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class RefundInfo {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;// id号
    private String orderNo;// 订单号
    private double redundantRecharge;// 多计费金额
    private double refundFee;// 实际退款金额
    private String refundType;// 退款方式
    private Date refundTime;// 退款时间
    private Date inertTime;// 插入时间
}
