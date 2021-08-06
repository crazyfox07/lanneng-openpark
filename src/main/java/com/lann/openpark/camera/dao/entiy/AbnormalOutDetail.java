package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "abnormal_out_detail")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class AbnormalOutDetail {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;// id号
    private String aid;// 异常驶离主表id
    private String plateNo;// 车牌号
    private String plateType;// 车牌类型
    private Date outTime;// 驶离时间
    private String imgUrl;// 驶离图片
    @Transient
    private String outTimeStr;// 显示时间，不做字段
}
