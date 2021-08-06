package com.lann.openpark.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    CHANGNEI("0", "场内"),
    SHIBIECC("7", "场内"),
    AUTO_GO("1", "自动放行"),
    MANAUL_GO("2", "人工放行"),
    FREE_GO("4", " 免费放行"),
    CLIENT_DEL("6", " 软件结束"),
    EPARK_GO("8", " 后台放行"),
    MANAUL_DEL("9", " 人工删除"),
    ;

    private String code;
    private String msg;


    OrderStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
