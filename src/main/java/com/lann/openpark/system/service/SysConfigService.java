package com.lann.openpark.system.service;

import com.lann.openpark.common.Constant;
import com.lann.openpark.common.response.ResponseResult;
import com.lann.openpark.system.dao.entiy.SysConfig;
import com.lann.openpark.system.dao.repository.SysConfigRepository;
import com.lann.openpark.util.EhcacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Transactional
@Service
public class SysConfigService {
    @Autowired
    SysConfigRepository sysConfigRepository;
    private static final Logger log = LoggerFactory.getLogger(SysConfigService.class);

    /**
     * 更新缓存信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/15 9:35
     **/
    public String updateSysConfig() {
        try {
            List<SysConfig> list = sysConfigRepository.findSysConfigAll();
            Map<String, String> collect = list.stream().collect(Collectors.toMap(SysConfig::getConfig_key, SysConfig::getConfig_value));
            // System.out.println(collect);
            // 将转换后的列表加入属性中
            Properties properties = new Properties();
            properties.putAll(collect);
            log.info("数据库读取配置信息：" + properties);
            // 将配置信息放到缓存
            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            ehcacheUtil.put(Constant.CONFIG_CACHE, "sys_properties", properties);
            return ResponseResult.SUCCESS().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.FAIL().toString();
        }
    }
}
