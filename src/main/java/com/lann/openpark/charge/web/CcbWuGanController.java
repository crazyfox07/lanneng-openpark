package com.lann.openpark.charge.web;

import com.lann.openpark.camera.threads.VoiceLedThread;
import com.lann.openpark.charge.service.CcbWuGanService;
import com.lann.openpark.util.UUIDUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CcbWuGanController {
    @Autowired
    private CcbWuGanService ccbWuGanService;

    @RequestMapping({"/ccb/wgzfycx"})
    public JSONObject wgzfycx() throws Exception {
        JSONObject result = new JSONObject();
        result.put("flag", true);
        result.put("err_msg", "");
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("carNo", "鲁L12345");
            // 建行无感支付预查询接口
            JSONObject data = ccbWuGanService.wgzfycx(jsonParam);
            result.put("data", data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("err_msg", e.toString());
            return result;
        }
    }

    @RequestMapping({"/ccb/wgzfkk"})
    public JSONObject wgzfkk() throws Exception {
        JSONObject result = new JSONObject();
        result.put("flag", true);
        result.put("err_msg", "");
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("ORDERID", UUIDUtils.getUUID());
            jsonParam.put("carNo", "鲁L71R86");
            jsonParam.put("AMOUNT", "0.01");
            // 建行无感支付预查询接口
            JSONObject data = ccbWuGanService.wgzfkk(jsonParam);
            result.put("data", data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("err_msg", e.toString());
            return result;
        }
    }

    @RequestMapping({"/voice_led"})
    public String voice_led(@RequestParam(name = "ledType") int ledType,
                            @RequestParam(name = "voiceType") int voiceType,
                            @RequestParam(name = "deviceNo") String deviceNo,
                            @RequestParam(name = "fee") double fee) {
        Thread thread = new Thread((Runnable) new VoiceLedThread("鲁LB1582", deviceNo,
                1, voiceType, ledType, null, 10000, fee));
        thread.start();
        return "success";
    }

    @RequestMapping({"/ccb_callback"})
    public String ccb_callback(@RequestParam(name = "ledType") int ledType,
                               @RequestParam(name = "voiceType") int voiceType,
                               @RequestParam(name = "deviceNo") String deviceNo,
                               @RequestParam(name = "fee") double fee) {
        Thread thread = new Thread((Runnable) new VoiceLedThread("鲁LB1582", deviceNo,
                1, voiceType, ledType, null, 10000, fee));
        thread.start();
        return "success";
    }

}
