package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "park_vip_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkVipInfo {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 12)
    private String vipId;
    private String carno;
    private String carnoType;
    private String name;
    private String sex;
    @Column(name = "age", columnDefinition = "tinyint default 0")
    private int age;
    private String licenceType;
    private String licenceNo;
    private String phone;
    private String carcolor;
    private String brand;
    private String brandSubType;
    private String vipType;
    private double vipDiscount;
    private Date validBegintime;
    private Date expiryDate;
    private double remainMoney;
    private String validScope;
    private String deleteStatus;
    private String freezeStatus;
    private Date freezeUntil;
    @Column(name = "valid", columnDefinition = "tinyint default 0")
    private int valid;
    private String cloudMember;
    private String groupNid;
}
