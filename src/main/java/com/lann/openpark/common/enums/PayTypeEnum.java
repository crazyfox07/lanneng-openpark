package com.lann.openpark.common.enums;

import lombok.Getter;

@Getter
public enum PayTypeEnum {
    ACCOUNT("1201", "账户储值"),
    ZHIFUBAO("1202", "支付宝"),
    CREDIT_CART("1203", "信用卡"),
    BANK_CART("1204", "银行卡"),
    WECHAT("1205", "微信"),
    CASH("1206", "现金"),
    PARK_CARD("1207", "停车卡"),
    THE_PUBLIC("1212", "公众号"),
    ETC("1229", "ETC支付"),
    ;

    private String code;
    private String msg;

    PayTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
