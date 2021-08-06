package com.lann.openpark.system.dao.entiy;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "sys_config")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class SysConfig {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String config_id;// 配置ID
    private String config_key;// 配置名
    private String config_value;// 配置值
    private String config_comment;// 配置注释
    private String config_state;// 启用标志
}
