package com.lann.openpark.common;


import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.util.EhcacheUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class ParkCommon {
    /**
     * 停车场特色功能，首次免费配置
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/2 10:19
     **/
    public static boolean getIsFreeTime(ParkChargeInfo parkChargeInfo) {
        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        boolean isFreeTime = false;
        String firstFree = properties.getProperty("first_free");
        if ("1".equals(firstFree)) {
            int inCounts = parkChargeInfo.getInCounts();
            if (inCounts >= 1) {
                isFreeTime = true;
            }
        }
        return isFreeTime;
    }
}
