package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "led_config")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class LedConfig {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;// id号
    private int lineId;// 行号
    private int disMode;// 显示模式
    private int delayTime;// 停留时间
    private int enterSpeed;// 显示速度
    private int displayNum;// 显示次数
    private int textColor;// 显示颜色
    private int type;// 类型1驶入，2驶离，，，
    private String text;// 显示的内容
}
