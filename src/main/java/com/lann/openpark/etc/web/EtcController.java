package com.lann.openpark.etc.web;

import com.lann.openpark.camera.web.CameraController;
import com.lann.openpark.common.response.ResponseResult;
import com.lann.openpark.etc.service.EtcService;
import com.lann.openpark.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Date;

@RestController
@Api(value = "ETC扣费对接")
public class EtcController {
    private static final Logger log = LoggerFactory.getLogger(CameraController.class);

    @Autowired
    EtcService etcService;

    @ApiOperation("ETC扣费下行接收方法")
    @RequestMapping(value = "/park/etcPayDetail/", produces = "text/plain;charset=UTF-8")
    public String receiveHandler(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer bodyString = new StringBuffer();
        String line = null;
        // 1.接收相机上报数据
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                bodyString.append(line);
            }
            return etcService.etcPayDetail(bodyString.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ETC扣费下行接收方法异常：" + DateUtil.formatDateYMDHMS(new Date()));
            return ResponseResult.FAIL().toString();
        }
    }

}
