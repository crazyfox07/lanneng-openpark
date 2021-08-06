package com.lann.openpark.util;


import org.apache.commons.codec.binary.Base64;

public class Base64Util {
    /**
     * base64转byte
     * @Author songqiang
     * @Description 
     * @Date 2020/6/10 15:48
    **/
    public static byte[] base64ToByteArr(String base64Str){
        return Base64.decodeBase64(base64Str);
    }
    
    /**
     * byte数组转base64字符串
     * @Author songqiang
     * @Description 
     * @Date 2020/6/10 15:50
    **/
    public static String byteArrToBase64(byte[] byteArr){
        return Base64.encodeBase64String(byteArr).replaceAll("\r\n","");
    }
}
