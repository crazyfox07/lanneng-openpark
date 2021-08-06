package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name="park_detect_info")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class ParkDetectInfo {

    @Id
//    @GeneratedValue(generator = "jpa-uuid")
//    @Column(length = 32)
    private String nid;// id号
    private String licensetype;// 常见号牌类型对应枚举：01：大型车  02：小型车
    private String vehicleType;// 车辆型号(7201：小型车；7202：中型车；7203：大型车；7204：新能源小型车；7205：新能源中型车；7206：新能源大型车)
    private String carno;// 号牌号码（主）
    private String carnosecond;// 号牌号码（辅）
    private String carnoOriginal;// 原始号牌号码，入库的时候同carno的值相同即可，用于保留原始的号牌识别记录。）
    private String pointcode;// 出入口编号
    private String pointname;// 出入口名称
    private String direction;// 方向编号，9进入停车场  10 离开停车场，出入口使用这两个枚举就OK
    private String devicecode;// 方向编号，9进入停车场  10 离开停车场，出入口使用这两个枚举就OK
    private String cpic1path;// 图片路径1
    private String cpic2path;// 图片路径2
    private String carcolor;// 车身颜色 A白色 B灰 C黄D粉E红F紫G绿H蓝I棕J黑Z其他，这里可以组合比如AB色
    private String platecolor;// 号牌颜色  0白色、1黄色、2蓝色、3黑色、4绿色、9其他
    private String platePosition;// 图片坐标
    private String carbrand;// 车标
    private String carversion;// 车辆的版型，如福特野马，福特猛禽、大众辉腾等
    private Date collectiondate;// 采集时间
    private Date savedate;// 入库时间
    private String parkingType;//
    private String editor;// 修改人
    private Date editdate;// 修改人
    private String region_code;// 区域编号
    private String groupNid;//
    private String vipOutnumber;//
    private int outType;// 是否异常驶离，驶离是未查询到订单"0"--正常，"1"异常

}
