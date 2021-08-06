package com.lann.openpark.util;

import net.sf.json.JSONObject;

/**
 * Created by songqiang on 2020/7/1.
 */
public class CameraUtil {
    /**
     * 返回开闸消息
     * @Author songqiang
     * @Description
     * @Date 2020/6/10 16:52
     **/
    public static String getOpenGateMsg(){
        return "{\"Response_AlarmInfoPlate\":{\"info\":\"ok\",\"content\":\"...\",\"is_pay\":\"true\"}}";
    }
    
    /**
     * 返回触发识别消息
     * @Author songqiang
     * @Description 
     * @Date 2020/7/13 16:06
    **/
    public static String getManualTriggerMsg(){
        return "{\"Response_AlarmInfoPlate\":{\"manualTrigger\":\"ok\",\"content\":\"...\",\"is_pay\":\"true\"}}";
    }
    
    /**
     * 开闸识别同时消息
     * @Author songqiang
     * @Description 
     * @Date 2020/7/13 16:08
    **/
    public static String getOpenGateAndManualTriggerMsg(){
        return "{\"Response_AlarmInfoPlate\":{\"manualTrigger\":\"ok\",\"info\":\"ok\",\"content\":\"...\",\"is_pay\":\"true\"}}";
    }

}
