package com.lann.openpark.common.enums;

import lombok.Getter;

@Getter
public enum DetectEnum {
    IN(9, "驶入"),
    OUT(10, "驶离"),
    ;

    private Integer code;
    private String msg;


    DetectEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
