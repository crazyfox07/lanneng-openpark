package com.lann.openpark.camera.threads;

import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.bean.EtcBean;
import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.repository.EquipSendMsgRepository;
import com.lann.openpark.camera.dao.repository.LedConfigRepository;
import com.lann.openpark.camera.dao.repository.VoiceConfigRepository;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.common.Constant;
import com.lann.openpark.etc.service.EtcService;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.util.ApplicationContextUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.websocket.WebSocketServer;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class EparkPayThread implements Runnable {

    private Logger log = LoggerFactory.getLogger(VoiceLedThread.class);

    private OpenEparkService openEparkService = ApplicationContextUtil.getBean(OpenEparkService.class);
    private EquipSendMsgRepository equipSendMsgRepository = ApplicationContextUtil.getBean(EquipSendMsgRepository.class);
    private LedConfigRepository ledConfigRepository = ApplicationContextUtil.getBean(LedConfigRepository.class);
    private VoiceConfigRepository voiceConfigRepository = ApplicationContextUtil.getBean(VoiceConfigRepository.class);
    private ParkChargeInfoRepository parkChargeInfoRepository = ApplicationContextUtil.getBean(ParkChargeInfoRepository.class);
    private EtcService etcService = ApplicationContextUtil.getBean(EtcService.class);

    private ParkChargeInfo parkChargeInfo;
    private EquipGateInfo equipGateInfo;
    private CameraData cameraData;
    private double parkingFee;
    private boolean isVoice;
    private ParkingInfo parkingInfo;
    private EtcBean etcBean;


    public EparkPayThread(ParkChargeInfo parkChargeInfo, double parkingFee, EquipGateInfo equipGateInfo, CameraData cameraData,
                          boolean isVoice, ParkingInfo parkingInfo, EtcBean etcBean) {

        this.parkChargeInfo = parkChargeInfo;
        this.parkingFee = parkingFee;
        this.equipGateInfo = equipGateInfo;
        this.cameraData = cameraData;
        this.isVoice = isVoice;
        this.parkingInfo = parkingInfo;
        this.etcBean = etcBean;
    }

    /**
     * Epark会员扣费线程执行方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/8/5 13:10
     **/
    public void run() {
        try {

            // 获取缓存读取工具
            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            // 读取ETC配置
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String isLeaguerFee = properties.getProperty("is_leaguer_fee");// is_leaguer_fee// 获取改停车场是否配置支持平台优惠券、停车卡的支持
            String parkCardConfig = properties.getProperty("is_park_card");// is_park_card
            // 桌面推送
            String isDesk = properties.getProperty("is_desk");
            if ("1".equals(isDesk)) {
                WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_OUT_MESSAGE, "等待支付", String.valueOf(parkingFee));
            }

            if ("1".equals(isLeaguerFee)) {
                // 查询会员信息
                String retStr = openEparkService.queryEparkLeaguerV19(parkChargeInfo);
                JSONObject jb_leaguer = JSONObject.fromObject(retStr);
                // System.out.println(jb_leaguer);

                boolean isLeaguerDeduction = false;
                // 判断会员信息
                if ("success".equals(jb_leaguer.getString("result")) && jb_leaguer.getJSONObject("responseBody").size() > 0) {
                    JSONObject responseBody = jb_leaguer.getJSONObject("responseBody");
                    if ("0".equals(parkCardConfig)) {// 停车场不支持优惠券、停车卡，判断订单金额和会员余额金额
                        if (responseBody.containsKey("leaguerSum")) {
                            // 获取会员充值余额
                            double leaguerSum = responseBody.getDouble("leaguerSum");
                            if (leaguerSum >= this.parkingFee) {// 会员余额大于停车费用
                                isLeaguerDeduction = true;
                            }
                        } else {
                            isLeaguerDeduction = true;
                        }

                    } else {// 停车场支持优惠券、停车卡
                        double currSum = responseBody.getDouble("currSum");
                        if (currSum >= this.parkingFee) {// 会员余额大于停车费用，发起会员扣费 mod by sq 20210224
                            isLeaguerDeduction = true;
                        } else {
                            isLeaguerDeduction = false;
                        }

                    }
                }
                if (isLeaguerDeduction) {// 是会员，因为没有返回会员下的停车卡和优惠券，所以无法根据余额判断是否请求扣费，是会员直接请求扣费
                    // 请求会员扣费
                    String retStr1 = "";
                    if ("0".equals(parkCardConfig)) {
                        retStr1 = openEparkService.orderChargeV19(parkChargeInfo, parkingFee, true);
                    } else {
                        retStr1 = openEparkService.orderChargeV19(parkChargeInfo, parkingFee, false);
                    }

                    // 请求订单扣费
                    JSONObject jb_charge = JSONObject.fromObject(retStr1);
                    if (!"success".equals(jb_charge.getString("result"))) {// 订单扣费失败
                        // 会员扣费失败走ETC扣费接口
                        if (etcBean.isOnEtc()) {
                            // ETC扣费请求，不再解析返回结果，由ETC下发返回结果 mod by sq 20200923
                            new Thread(new Runnable() {
                                public void run() {
                                    etcService.etcCharge(equipGateInfo, parkingInfo, parkingFee, parkChargeInfo, cameraData, etcBean, isVoice);
                                }
                            }).start();
                            if (isVoice) {
                                // 下发语音和LED消息
                                // voiceType = 4
                                // ledType = 2
                                Thread thread = new Thread((Runnable) new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 4, 2, null, 1000, parkingFee));
                                thread.start();
                            }
                        } else {
                            if (isVoice) {
                                // 下发语音和LED消息
                                // voiceType = 4
                                // ledType = 2
                                Thread thread = new Thread((Runnable) new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 4, 2, null, 1000, parkingFee));
                                thread.start();
                            }
                        }
                    }
                } else {
                    // 非会员走ETC接口
                    if (etcBean.isOnEtc()) {
                        new Thread(new Runnable() {
                            public void run() {
                                etcService.etcCharge(equipGateInfo, parkingInfo, parkingFee, parkChargeInfo, cameraData, etcBean, isVoice);
                            }
                        }).start();
                        if (isVoice) {
                            // 下发语音和LED消息
                            // voiceType = 4
                            // ledType = 2
                            Thread thread = new Thread((Runnable) new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 4, 2, null, 1000, parkingFee));
                            thread.start();
                        }
                    } else {
                        if (isVoice) {
                            // 下发语音和LED消息
                            // voiceType = 4
                            // ledType = 2
                            Thread thread = new Thread((Runnable) new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 4, 2, null, 1000, parkingFee));
                            thread.start();
                        }
                    }
                }
            } else {
                // 非会员走ETC接口
                if (etcBean.isOnEtc()) {
                    new Thread(new Runnable() {
                        public void run() {
                            etcService.etcCharge(equipGateInfo, parkingInfo, parkingFee, parkChargeInfo, cameraData, etcBean, isVoice);
                        }
                    }).start();
                    if (isVoice) {
                        // 下发语音和LED消息
                        // voiceType = 4
                        // ledType = 2
                        Thread thread = new Thread((Runnable) new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 4, 2, null, 1000, parkingFee));
                        thread.start();
                    }
                } else {
                    if (isVoice) {
                        // 下发语音和LED消息
                        // voiceType = 4
                        // ledType = 2
                        Thread thread = new Thread((Runnable) new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 4, 2, null, 1000, parkingFee));
                        thread.start();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("【执行Epark会员扣费】线程异常", e);
        }
    }


}
