package com.lann.openpark.websocket.web;

import com.lann.openpark.websocket.WebSocketServer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RestController
@RequestMapping("/checkcenter")
public class CheckCenterController {

    @GetMapping("/socket/{cid}")
    public ModelAndView socket(@PathVariable String cid) {
        ModelAndView mav = new ModelAndView("/socket");
        mav.addObject("cid", cid);
        return mav;
    }

    @ResponseBody
    @RequestMapping("/socket/push/{cid}")
    public String pushToWeb(@PathVariable String cid, String message) {
        try {
            WebSocketServer.sendMassMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            return "error:" + cid + "#" + e.getMessage();
        }
        return "success:" + cid;
    }

}

