package com.lann.openpark.camera.web;

import com.lann.openpark.camera.service.CameraService;
import com.lann.openpark.common.response.ResponseResult;
import com.lann.openpark.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Date;


@RestController
@Api(value = "相机数据上报")
public class CameraController {

    private static final Logger log = LoggerFactory.getLogger(CameraController.class);

    @Autowired
    CameraService cameraService;

    /**
     * 相机数据上报
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 17:27
     **/
    @ApiOperation("停车场相机数据上报接口")
    @RequestMapping(value = "/camera/camData", produces = "text/plain;charset=UTF-8")
    public String camData(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer jsonStr = new StringBuffer();
        String line = null;
        // 1.接收相机上报数据
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jsonStr.append(line);
            }
            // log.info("相机上报数据接收：" + jsonStr.toString());
            return cameraService.camData(jsonStr.toString());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("停车场相机数据上报接口" + DateUtil.formatDateYMDHMS(new Date()));
            return ResponseResult.FAIL().toString();
        }
    }

    /**
     * RM相机comet轮询接口
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 17:50
     **/
    @ApiOperation("停车场相机comet轮询")
    @RequestMapping(value = "/camera/parkComet", produces = "text/plain;charset=UTF-8")
    public String parkComet(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "device_name") String device_name,
                            @RequestParam(value = "ipaddr") String ipaddr, @RequestParam(value = "port") int port, @RequestParam(value = "user_name") String user_name,
                            @RequestParam(value = "pass_wd") String pass_wd, @RequestParam(value = "serialno") String serialno, @RequestParam(value = "channel_num") int channel_num) throws Exception {
        return cameraService.parkComet(serialno);
    }

    /**
     * 测试方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/6 16:28
     **/
//    @RequestMapping(value = "/test", produces = "text/json;charset=UTF-8")
//    public String testCam(HttpServletRequest request, HttpServletResponse response) {
//        StringBuffer jsonStr = new StringBuffer();
//        String line = null;
//        try {
//            BufferedReader reader = request.getReader();
//            while ((line = reader.readLine()) != null) {
//                jsonStr.append(line);
//            }
//            System.out.println(jsonStr.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("测试接口：" + DateUtil.formatDateYMDHMS(new Date()));
//            return ResponseResult.FAIL().toString();
//        }
//        return ResponseResult.SUCCESS().toString();
//    }


}
