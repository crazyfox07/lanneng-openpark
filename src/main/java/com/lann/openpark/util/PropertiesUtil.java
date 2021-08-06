package com.lann.openpark.util;

import com.lann.openpark.common.Constant;

import java.util.Properties;

public class PropertiesUtil {

    /**
     * 获取系统配置属性值
     *
     * @Author songqiang
     * @Description
     * @Date 2021/7/27 14:43
     **/
    public static String getPropertiesByName(String propertiesByName) {
        // 缓存
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String propertiesValue = properties.getProperty(propertiesByName);
        return propertiesValue;
    }
}
