package com.lann.openpark.foreign.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class OrderBean {
    private String orderNo;
    private String plateNo;
    private String plateType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeIn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date timeOut;
    private int parkDuration;
    private String orderStatus;
    private Double orderCharge;
    private Double payCharge;
    private String inPicPath;
    private String outPicPath;
}
