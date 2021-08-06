package com.lann.openpark.system.web;

import com.lann.openpark.camera.web.CameraController;
import com.lann.openpark.common.Constant;
import com.lann.openpark.system.service.SysConfigService;
import com.lann.openpark.util.EhcacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

@RestController
@Api(value = "配置信息")
public class SysConfigController {
    private static final Logger log = LoggerFactory.getLogger(CameraController.class);

    @Autowired
    SysConfigService sysConfigService;

    @ApiOperation("更新数据库配置信息到内存")
    @RequestMapping(value = "/sysconfig/updateSysConfig")
    public String updateSysConfig(HttpServletRequest request, HttpServletResponse response) {
        return sysConfigService.updateSysConfig();
    }

    @RequestMapping(value = "/sysconfig/testConfig")
    public String testConfig(HttpServletRequest request, HttpServletResponse response) {
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        return properties.getProperty("file_upload_suffix");
    }
}
