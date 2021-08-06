package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "voice_config")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class VoiceConfig {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;// id号
    private int type;// 类型1临时车驶入2会员车驶入3临时车驶离4会员驶离5etc扣费
    private String template;
}
