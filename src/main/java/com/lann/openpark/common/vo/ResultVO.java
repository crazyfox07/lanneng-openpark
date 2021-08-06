package com.lann.openpark.common.vo;

import lombok.Data;

/**
 * 返回信息格式
 *
 * @Author songqiang
 * @Description
 * @Date 2021/7/30 10:29
 **/
@Data
public class ResultVO<T> {
    private Integer code;
    private String message;
    private T data;
}
