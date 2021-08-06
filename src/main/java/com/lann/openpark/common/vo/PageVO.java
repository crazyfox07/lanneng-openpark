package com.lann.openpark.common.vo;

import lombok.Data;

/**
 * 分页数据返回格式
 *
 * @Author songqiang
 * @Description
 * @Date 2021/7/30 10:29
 **/
@Data
public class PageVO<T> {
    private Integer code;
    private String message;
    private T data;
    private Long total;
    private Integer pagenum;
    private Integer pagesize;
}
