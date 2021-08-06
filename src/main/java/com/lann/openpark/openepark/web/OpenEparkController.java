package com.lann.openpark.openepark.web;

import com.alibaba.fastjson.JSONObject;
import com.lann.openpark.camera.web.CameraController;
import com.lann.openpark.common.response.ResponseResult;
import com.lann.openpark.openepark.service.OpenEparkService;
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
@Api(value = "好停车平台对接")
public class OpenEparkController {
    private static final Logger log = LoggerFactory.getLogger(CameraController.class);

    @Autowired
    OpenEparkService openEparkService;

    /**
     * 好停车下行接收方方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/9 15:52
     **/
    @ApiOperation("好停车下行接收方法")
    @RequestMapping(value = "/park/receiveHandler/", produces = "text/plain;charset=UTF-8")
    public String receiveHandler(HttpServletRequest request, HttpServletResponse response) {
        HttpServletRequest request1 = (HttpServletRequest) request;
        String method = request1.getMethod();
        if ("GET".equals(method)) {// 服务对接验证，openEpark会请求这个GET地址验证
            JSONObject jb = new JSONObject();
            jb.put("result", "success");
            jb.put("msg", "success");
            return jb.toJSONString();
        } else if ("POST".equals(method)) {// POST请求上行openEpark进行对接验证
            StringBuffer bodyString = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    bodyString.append(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("好停车下行数据接收异常：" + DateUtil.formatDateYMDHMS(new Date()));
                return ResponseResult.FAIL().toString();
            }
            return openEparkService.receiveHandler(bodyString.toString());
        } else {
            return ResponseResult.FAIL().toString();
        }
    }

    /**
     * 停车场上行接入验证
     * 第一次启用的时候，需要在浏览器中输入以下地址，进行验证
     * 验证地址：http://127.0.0.1:9000/park/parkVerify/
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 15:17
     **/
    @ApiOperation("好停车下行接收方法")
    @RequestMapping(value = "/park/parkVerify")
    public String parkVerify() {
        return openEparkService.verifyServer();
    }

    /**
     * 停车场域绑定
     * 第一次启用的时候，需要在浏览器中输入以下地址，进行验证
     * 绑定地址：http://127.0.0.1:9000/park/bindPark/
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/9 16:06
     **/
    @ApiOperation("停车场域绑定")
    @RequestMapping(value = "/park/bindPark")
    public String bindPark(HttpServletRequest request, HttpServletResponse response) {
        return openEparkService.bindPark();
    }

    /**
     * 设备信息上传
     * 第一次启用（或者设备更换）的时候，需要在浏览器中输入以下地址，进行验证
     * 上传地址：http://127.0.0.1:9000/park/uploadDeviceInfo/
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/14 9:48
     **/
    @ApiOperation("设备信息上传")
    @RequestMapping(value = "/park/uploadDeviceInfo")
    public String uploadDeviceInfo(HttpServletRequest request, HttpServletResponse response) {
        openEparkService.uploadDeviceInfoTask();
        return ResponseResult.SUCCESS().toString();
    }

}
