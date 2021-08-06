package com.lann.openpark.foreign.bean;

import lombok.Data;

@Data
public class RemainSpaceBean {
    private String parkCode;
    private int totalSpace;
    private int remainSpace;
}
