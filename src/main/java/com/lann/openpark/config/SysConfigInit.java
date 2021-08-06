package com.lann.openpark.config;

import com.lann.openpark.common.Constant;
import com.lann.openpark.system.dao.entiy.SysConfig;
import com.lann.openpark.system.dao.repository.SysConfigRepository;
import com.lann.openpark.util.EhcacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Configuration
@Component
public class SysConfigInit {
    @Autowired
    SysConfigRepository sysConfigRepository;

    @PostConstruct
    public void initSysConfig() {
        List<SysConfig> list = sysConfigRepository.findSysConfigAll();
        Map<String, String> collect = list.stream().collect(Collectors.toMap(com.lann.openpark.system.dao.entiy.SysConfig::getConfig_key, com.lann.openpark.system.dao.entiy.SysConfig::getConfig_value));
        // System.out.println(collect);
        // 将转换后的列表加入属性中
        Properties properties = new Properties();
        properties.putAll(collect);
        System.out.println("数据库读取配置信息：" + properties);
        // 将配置信息放到缓存
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        ehcacheUtil.put(Constant.CONFIG_CACHE, "sys_properties", properties);
    }
}
