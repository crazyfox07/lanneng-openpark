package com.lann.openpark.client.web;

import com.lann.openpark.client.service.PlatenullService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

@Controller
@Api(value = "无牌车接口")
public class PlatenullController {
    private static final Logger log = LoggerFactory.getLogger(PlatenullController.class);

    @Autowired
    PlatenullService platenullService;

    /**
     * 无牌车驶入接口
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/12 9:49
     **/
    // @RequestMapping(value = "/scanin")
    public String noplatein(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response, String parkCode, String deviceCode) {
        // JSONObject jb = platenullService.noplatein(parkCode, deviceCode);
//        if ("0".equals(jb.getString("code"))) {// 跳转成功页面
//            modelMap.addAttribute("code", jb.getString("code"));
//            modelMap.addAttribute("msg", "临时车牌号：" + jb.getString("tempPlateNo"));
//            modelMap.addAttribute("tempPlateNo", jb.getString("tempPlateNo"));
//            modelMap.addAttribute("plateType", jb.getString("plateType"));
//            return "info";
//        } else {// 跳转失败页面
//            modelMap.addAttribute("code", jb.getString("code"));
//            return "info";
//        }
        return "";
    }

    // @RequestMapping(value = "/wodeceshi")
    public String test(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {
//        modelMap.addAttribute("msg", "临时车牌号：好L12345");
//        modelMap.addAttribute("msg", "驶入失败，请重新扫码");

        modelMap.addAttribute("code", "0");
        modelMap.addAttribute("msg", "临时车牌号：鲁L12345");
        modelMap.addAttribute("tempPlateNo", "鲁L12345");
        modelMap.addAttribute("plateType", "1");

//        modelMap.addAttribute("code", "1");
//        modelMap.addAttribute("msg", "驶入失败，请重新扫码");
        return "info";
    }
}
