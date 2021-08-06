package com.lann.openpark.client.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 停车场区域信息
 *
 * @Author songqiang
 * @Description
 * @Date 2020/10/29 14:26
 **/
@Data
@ToString
@Entity
@Table(name = "park_region_info")
public class ParkRegionInfo implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 12)
    private String regionCode;
    private String regionName;
    private String parkcode;
    private Integer berthCount;
    private String chargeSchemeNid;
    private Integer regionRectifyCount;
    private Integer judgeRemainParkingNumber;
    private String limitPlatetype;
    private String limitOther;

    // mod by sq 20210802
    private String regionType;// 区域类型
    private String parentRegion;// 父区域
    private String parentRegionName;// 父区域名称

    // 将车辆限制进入放入区域上设置
    private String restrictedAccess;// 限制进入
    private String whitelistPrivileges;// 白名单特权

}
