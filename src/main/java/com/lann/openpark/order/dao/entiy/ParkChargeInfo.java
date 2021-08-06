package com.lann.openpark.order.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "park_charge_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkChargeInfo {

    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String nid;// id号
    private String enterNid;// 驶入记录nid
    private String exitNid;// 驶离记录nid
    private Integer parkDuration;// 停车时长
    private float totalcharge;// 总费用
    private float charge;// 应收
    private float realcharge;// 实收
    private float derateFee;// 打折的费用
    private float couponFee;// 优惠券的费用
    private float discount;// 折扣率
    private String payType;
    // 支付方式：1现金、2支付宝、3微信、4银联支付、5储值扣费、6其他
    // 1201 会员余额支付
    // 1202 支付宝
    // 1204 银行卡
    // 1205 微信
    // 1206 现金
    // 1211 停车卡
    // 1226 商户代扣
    // 1227 建行龙支付
    private String deductionType;// 抵扣方式，如小票、优惠券
    private float deductionMoney;// 抵扣金额
    private float remainMoney;// 储值车辆的余额
    private String operator;// 0系统，其他代表userid
    private String parkingType;// 停车类型(1：白名单车辆、2：储值车辆、3：包月车辆、4：临时会员车辆；
    // 5：云端会员车辆；6：共享预约；7：共享包月；8、出租车9：临时车 0：特殊车辆)
    private String exitType;// 离场类型1自动放行、2人工收费放行、3特殊车辆放行、4免费放行、5凭证放行
    private String vehicleType;// 车辆型号(7201：小型车；7202：中型车；7203：大型车；7204：新能源小型车；7205：新能源中型车；7206：新能源大型车)
    private String exitComment;// 备注信息
    private String freeComment;// 备注信息
    private String proofUrl;// 凭证图片
    private String chargeDetail;// 收费文字描述
    private Date recordTime;// 该条记录生成时间
    private String licensetype;// 常见号牌类型对应枚举：01：大型车  02：小型车
    private String plateType;// 车牌类型
    private String carno;// 车牌号
    private String carnoOriginal;// 原始车牌号
    private String pointcodeEnt;// 入口 区域编号
    private String pointcodeExt;// 出口 区域编号
    private String pointnameEnt;// 入口 区域名字
    private String pointnameExt;// 出口 区域名字
    private String devicecode;// 入口设备编号
    private String devicecodeExt;// 出口设备编号
    private String cpicEnterPath;// 驶入图片
    private String cpicExitPath;// 驶出图片
    private String carcolor;// 车身颜色 A白色 B灰 C黄D粉E红F紫G绿H蓝I棕J黑Z其他，这里可以组合比如AB色
    private String platePosition;// 图片坐标
    private Date collectiondate1;// 入口采集时间
    private Date collectiondate2;// 出口采集时间

    private Integer inUpload;// 驶入记录上传标志
    private Integer outUpload;// 驶离记录上传标志
    private Integer payUpload;// 支付记录上传标志

    private String inRandom;// 驶入上传更新随机数
    private String outRandom;// 驶离上传更新随机数
    private String payRandom;// 支付上传更新随机数

    private String eparkLeaguerId;// epark会员编号
    private Integer orderType;// 订单类型
    private Date payTime;// 支付时间

    private String vipId;// vipID

    private int inCounts;// 当日驶入次数，默认-1

    private String isFristDayFree;// 首日免费配置
    private String isLastDayFree;// 最后一日免费配置


}
