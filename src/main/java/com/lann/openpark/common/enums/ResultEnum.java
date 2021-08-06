package com.lann.openpark.common.enums;

import lombok.Getter;

@Getter
public enum ResultEnum {
    SUCCESS(0, "请求成功"),
    PARAM_ERROR(1, "参数错误"),
    EXCEPTION(99, "请求异常"),


    // 对外接口
    PAGE_SIZE_OVER100(901, "每页数据不能超过100条"),

    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

