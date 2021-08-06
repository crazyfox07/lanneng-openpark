package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "led_base_show")
public class LedBaseShow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cameraId;// 相机编号
    private Integer lineNum;// 行数 4行屏幕，0，1，2，3，4
    private String text; // 显示内容
    private Integer disMode;// 显示模式
    private Integer enterSpeed;// 显示的速度
    private Integer delayTime;// 停留时间 秒
    private Integer textColor;// 字体的颜色
    private Integer disTimes;// 显示的次数
    private Integer funType; //类型
}
