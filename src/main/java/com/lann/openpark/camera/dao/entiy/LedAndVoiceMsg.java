package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class LedAndVoiceMsg implements Serializable {
    private String devicecode;
    private Integer channel;
    private String content;
    private Integer dataLength;
}
