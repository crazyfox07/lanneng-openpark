package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Entity
@Table(name = "camera_data")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class CameraData implements Serializable {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;// id号
    private String serialNo;// 设备序列号，设备唯一
    private String deviceName;// 设备名称
    private String ipAddr;// 设备IP地址
    private int colorType;// 车牌颜色 0：未知、1：蓝色、2：黄色、3：白色、4：黑色、5：绿色
    private int plateType;// 车牌类型 0：未知车牌:、1：蓝牌小汽车、2：:黑牌小汽车、3：单排黄牌、4：双排黄牌、 5：警车车牌、6：武警车
    //牌、7：个性化车牌、8：单排军车牌、9：双排军车牌、10：使馆车牌、11：香港进出中国大陆车牌、12：农用车
    private String license;
    private int confidence; // 识别结果可信度 1-100
    private int direction; // 车的行进方向，0：未知，1：左，2：右，3：上， 4：下
    private String location; // 车牌在图片中位置，json  { "RECT": { "bottom": 716, "left": 888, "right": 995, "top": 678 } }
    private int timeUsed; // 识别所用时间
    private String timeStamp; // 识别结果对应帧的时间戳
    private int triggerType;// 当前结果的触发类型：1：自动触发类型、2：外部输入触发（IO 输入）、4：软件触发（SDK）、8：虚拟线圈触发
    private String isFakePlate;// 是否伪车牌，0：真实车牌，1：伪车牌
    private byte[] imageFile;// 识别大图片内容经过 base64 后的字符串
    private int imageFileLen;// 识别大图片内容长度，注意不是 base64 后的长度
    private byte[] imageFragmentFile;// 识别车牌小图片内容经过 base64 后的字符串
    private int imageFragmentFileLen;// 识别小图片内容长度，注意不是 base64 后的长度
    // 断网重传
    private int isoffline;// 设备离线状态，0：在线，1：离线
    private int plateid;// 是否伪车牌，0：真实车牌，1：伪车牌
    private String gioouts;// 是否伪车牌，0：真实车牌，1：伪车牌
    private Date insertTime;// 插入时间

}
