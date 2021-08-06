package com.lann.openpark.camera.dao.entiy;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class GateMsg implements Serializable {
    private String devicecode;
}
