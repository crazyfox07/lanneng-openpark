package com.lann.openpark.park.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SysConfigBean {
    private String configId;
    private String configKey;
    private String configValue;
    private String configComment;
    private String configState;
}
