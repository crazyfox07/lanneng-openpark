package com.lann.openpark.common.enums;

import lombok.Getter;

/**
 * 车牌类型枚举
 *
 * @Author songqiang
 * @Description
 * @Date 2021/6/2 15:05
 **/
@Getter
public enum PlateTypeEnum {
    BLUE(1, "蓝牌"),
    YELLOW(3, "黄牌"),
    GREEN(19, "新能源"),
    ;

    private Integer code;
    private String msg;

    PlateTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
