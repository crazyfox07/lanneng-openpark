package com.lann.openpark.websocket;

import com.lann.openpark.common.Constant;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.util.DateUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/parksocket")
@Slf4j
@Data
@ToString
public class WebSocketServer {

    // 存储所有连接websocket的session
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    // websocket连接session
    private Session session;

    /**
     * websocket连接，接收订单推送
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 9:57
     **/
    @OnOpen
    public void onOpen(Session session) {

//        WebSocketService webSocketService = ApplicationContextUtil.getBean(WebSocketService.class);
//        webSocketService.testWs();
        // log.info("有桌面程序进入监控页面");
        this.session = session;
        // 添加set
        webSocketSet.add(this);
//        try {
//            sendMessage("websocket连接成功");
//        } catch (IOException e) {
//            log.error("websocket IO异常");
//        }
    }

    /**
     * 连接断开的方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 9:58
     **/
    @OnClose
    public void onClose() {
        // log.info("有桌面程序离开监控页面");
        // set中删除
        webSocketSet.remove(this);
    }


    /**
     * 消息接收
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 9:59
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("接收到消息:" + message);
//        for (WebSocketServer item : webSocketSet) {
//            try {
//                item.sendMassMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 连接错误
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 9:59
     **/
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket连接异常");
        error.printStackTrace();
    }

    /**
     * 消息发送
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 10:00
     **/
    public void sendMessage(Session session, String message) throws IOException {
        // 多线程调用该方法时，报错，添加synchronized关键字，控制 mod by sq 20210517
        synchronized (this.session) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("【websocket发送信息】异常，msg={} ", message);
                e.printStackTrace();
            }
        }

    }

    /**
     * 群发消息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/30 10:01
     **/
    public static void sendMassMessage(String message) throws IOException {
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(item.session, message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 桌面消息websocket推送
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/1 14:02
     **/
    public static void sendDeskMessage(ParkChargeInfo parkChargeInfo, String messageType, String payType, String payFee) {
        try {
            JSONObject jbSend = new JSONObject();
            jbSend.put("messageType", messageType);// 消息类型
            jbSend.put("orderNo", parkChargeInfo.getNid());// 订单编号
            jbSend.put("plateNo", parkChargeInfo.getCarno());// 车牌号码
            String timeIn = DateUtil.formatDateYMDHMS(parkChargeInfo.getCollectiondate1());
            jbSend.put("timeIn", timeIn);// 驶入时间
            if (Constant.SEND_IN_MESSAGE.equals(messageType)) {
                jbSend.put("deviceCode", parkChargeInfo.getDevicecode());// 设备号码
            } else {
                jbSend.put("deviceCode", parkChargeInfo.getDevicecodeExt());// 设备号码
            }

            if (Constant.SEND_OUT_MESSAGE.equals(messageType) || Constant.SEND_PAY_MESSAGE.equals(messageType)) {
                String timeOut = DateUtil.formatDateYMDHMS(parkChargeInfo.getCollectiondate2());
                jbSend.put("timeOut", timeOut);// 驶入时间
                jbSend.put("payFee", payFee);// 订单费用
                jbSend.put("payType", payType);// 支付方式
                jbSend.put("plateType", Constant.getCarTypeName(parkChargeInfo.getLicensetype()));
                jbSend.put("duration", parkChargeInfo.getParkDuration());
            }
            // 群发消息
            sendMassMessage(jbSend.toString());
        } catch (IOException e) {
            log.error("");
            e.printStackTrace();
        }
    }
}

