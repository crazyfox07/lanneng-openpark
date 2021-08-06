package com.lann.openpark.common.exception;


import com.lann.openpark.common.enums.ResultEnum;

public class ParkException extends RuntimeException {
    private Integer code;

    public ParkException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ParkException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
