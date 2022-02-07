package com.lann.openpark.camera.threads;

import com.lann.openpark.camera.bean.LedConfigBean;
import com.lann.openpark.camera.bean.ParkVipBean;
import com.lann.openpark.camera.dao.entiy.LedAndVoiceMsg;
import com.lann.openpark.camera.dao.entiy.LedConfig;
import com.lann.openpark.camera.dao.entiy.VoiceConfig;
import com.lann.openpark.camera.dao.repository.LedConfigRepository;
import com.lann.openpark.camera.dao.repository.VoiceConfigRepository;
import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import com.lann.openpark.client.dao.repository.ParkRegionInfoRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.util.ApplicationContextUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.VoiceLedUtil;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 语音下发线程
 *
 * @Author songqiang
 * @Description
 * @Date 2020/7/29 15:44
 **/
public class VoiceLedThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(VoiceLedThread.class);

    private LedConfigRepository ledConfigRepository = ApplicationContextUtil.getBean(LedConfigRepository.class);
    private VoiceConfigRepository voiceConfigRepository = ApplicationContextUtil.getBean(VoiceConfigRepository.class);

    private String plateNo;// 车牌号

    private String deviceNo;// 摄像机ID

    private int voiceChannel;// 声音频道

    private int voiceType;// 语音类型

    private int ledType;// led类型

    private int delays;// 延迟描述

    // private boolean isVip;// 是否是VIP
    ParkVipBean parkVipBean;

    private float fee;// 费用


    /**
     * 构造方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:44
     **/
    public VoiceLedThread(String plateNo, String deviceNo, int voiceChannel, int voiceType, int ledType, ParkVipBean parkVipBean, int delays, double fee) {
        this.plateNo = plateNo;
        this.deviceNo = deviceNo;
        this.voiceType = voiceType;
        this.ledType = ledType;
        this.delays = delays;
        // this.isVip = isVip;
        this.parkVipBean = parkVipBean;
        this.voiceChannel = voiceChannel;
        this.fee = Double.valueOf(fee).floatValue();
    }

    /**
     * 线程运行方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:44
     **/
    public void run() {
        try {
            // Thread.sleep(delays);// 线程休眠
            String voiceMsg = "";
            // 根据语音播报类型查询语音模板
            VoiceConfig voiceConfig = voiceConfigRepository.findVoiceConfigByType(voiceType);
            List<LedConfig> listTmp = ledConfigRepository.findLedConfigByType(ledType);
            // 转换数据
            List<LedConfigBean> list = this.covertLedConfigBean(listTmp);
            if (1 == voiceType || 2 == voiceType) {// 驶入
                voiceMsg = voiceConfig.getTemplate();
                // 替换文本
                voiceMsg = voiceMsg.replace("${plateNo}", plateNo);
                if (2 == voiceType) {
                    // 计算会员车剩余天数
                    Date expiryDate = parkVipBean.getExpiryDate();
                    int remain_days = getRemainDays(expiryDate, new Date());
                    voiceMsg = voiceMsg.replace("${emain_days}", remain_days + "");
                }
            } else if (6 == voiceType) {// 临时车、固定车驶离
                voiceMsg = voiceConfig.getTemplate();
                voiceMsg = voiceMsg.replace("${plateNo}", plateNo);
                if (null != parkVipBean) {
                    voiceMsg = voiceMsg.replace("${isVip}", "月租车");
                    // 替换
                    // 计算会员车剩余天数
                    Date expiryDate = parkVipBean.getExpiryDate();
                    int remain_days = getRemainDays(expiryDate, new Date());
                    voiceMsg = voiceMsg.replace("${tip_msg}", "剩余" + remain_days + "天");
                } else {
                    voiceMsg = voiceMsg.replace("${isVip}", "临时车");
                    // 替换
                    voiceMsg = voiceMsg.replace("${tip_msg}", "已缴费" + fee + "元");
                }
            } else if (4 == voiceType || 7 == voiceType || 3 == voiceType) {// 好停车会员驶离
                voiceMsg = voiceConfig.getTemplate();
                voiceMsg = voiceMsg.replace("${plateNo}", plateNo);
                voiceMsg = voiceMsg.replace("${fee}", fee + "");
            } else if (8 == voiceType || 9 == voiceType || 10 == voiceType) {
                voiceMsg = voiceConfig.getTemplate();
            } else if (5 == voiceType) {
                voiceMsg = voiceConfig.getTemplate();
                voiceMsg = voiceMsg.replace("${plateNo}", plateNo);
            } else if (14 == voiceType) {
                voiceMsg = voiceConfig.getTemplate();
                voiceMsg = voiceMsg.replace("${plateNo}", plateNo);
                voiceMsg = voiceMsg.replace("${fee}", fee + "");
            }

            //////////LED显示消息///////////////////////////////
            List<LedConfigBean> list1 = new ArrayList<>();
            if (1 == ledType || 2 == ledType || 4 == ledType) {// 支付
                list1.add(this.getLineBynum(list, 0));
                LedConfigBean ledConfig1 = this.getLineBynum(list, 1);
                String text = ledConfig1.getText();
                text = text.replace("${fee}", fee + "");
                ledConfig1.setText(text);
                list1.add(ledConfig1);
                LedConfigBean ledConfig2 = this.getLineBynum(list, 2);
                // 获取内容替换
                text = ledConfig2.getText();
                text = text.replace("${plateNo}", plateNo);
                ledConfig2.setText(text);
                list1.add(ledConfig2);
                list1.add(this.getLineBynum(list, 3));
            } else if (7 == ledType) {
                list1.add(this.getLineBynum(list, 0));
                LedConfigBean ledConfig1 = this.getLineBynum(list, 1);
                // 获取内容替换
                String text = ledConfig1.getText();
                text = text.replace("${plateNo}", plateNo);
                ledConfig1.setText(text);
                list1.add(ledConfig1);


                LedConfigBean ledConfig2 = this.getLineBynum(list, 2);
                text = ledConfig2.getText();
                text = text.replace("${fee}", fee + "");
                ledConfig2.setText(text);
                list1.add(ledConfig2);

                LedConfigBean ledConfig3 = this.getLineBynum(list, 3);
                text = ledConfig3.getText();
                if (null != parkVipBean) {
                    Date expiryDate = parkVipBean.getExpiryDate();
                    int remain_days = getRemainDays(expiryDate, new Date());
                    text = text.replace("${isVip}", "月租车，剩余" + remain_days + "天");
                } else {
                    text = text.replace("${isVip}", "临时车");
                }
                ledConfig3.setText(text);
                list1.add(ledConfig3);

            } else if (5 == ledType) {// 驶入
                // 第三行第四行是需要替换
                list1.add(this.getLineBynum(list, 0));
                LedConfigBean ledConfig1 = this.getLineBynum(list, 1);
                String text = ledConfig1.getText();
                text = text.replace("${plateNo}", plateNo);
                // 获取内容替换
                if (null != parkVipBean) {
                    Date expiryDate = parkVipBean.getExpiryDate();
                    int remain_days = getRemainDays(expiryDate, new Date());
                    text = text.replace("${isVip}", "月租车，" + "剩余" + remain_days + "天");
                } else {
                    text = text.replace("${isVip}", "临时车");
                }
                ledConfig1.setText(text);
                list1.add(ledConfig1);

                ParkRegionInfoRepository regionInfoRepository = ApplicationContextUtil.getBean(ParkRegionInfoRepository.class);
                // 获取区域信息
                int remianBerths = 0;
                ParkRegionInfo region = regionInfoRepository.findRegionInfoByDeviceCode(deviceNo);
                if (null != region) {
                    remianBerths = region.getRegionRectifyCount();
                }
                LedConfigBean ledConfig2 = this.getLineBynum(list, 2);
                text = ledConfig2.getText();
                text = text.replace("$(remainBerths)", remianBerths + "");
                ledConfig2.setText(text);
                list1.add(ledConfig2);
                list1.add(this.getLineBynum(list, 3));
            } else if (6 == ledType) {// 永久广告语

            } else if (8 == ledType || 9 == ledType || 10 == ledType
                    || 11 == ledType || 12 == ledType || 13 == ledType) {
                list1.add(this.getLineBynum(list, 0));
                list1.add(this.getLineBynum(list, 1));
                list1.add(this.getLineBynum(list, 2));
                list1.add(this.getLineBynum(list, 3));
            } else if (14 == ledType) {  // 建行无感支付
                LedConfigBean ledConfig14_1 = this.getLineBynum(list, 1);
                ledConfig14_1.setText(ledConfig14_1.getText().replace("${plateNo}", plateNo));
                list1.add(ledConfig14_1);
                list1.add(this.getLineBynum(list, 2));
                LedConfigBean ledConfig14_3 = this.getLineBynum(list, 3);
                ledConfig14_3.setText(ledConfig14_3.getText().replace("${fee}", String.valueOf(fee)));
                list1.add(ledConfig14_3);
            }

            Map map = VoiceLedUtil.getVoicrAndLedStr(list1, voiceMsg, 0);
            if ((int) map.get("length") > 0) {
                int length = (int) map.get("length");
                String baseStr = (String) map.get("baseStr");
                // 将数据存入ehcache缓存
                LedAndVoiceMsg ledAndVoiceMsg = new LedAndVoiceMsg();
                ledAndVoiceMsg.setDevicecode(deviceNo);
                ledAndVoiceMsg.setChannel(voiceChannel);
                ledAndVoiceMsg.setContent(baseStr);
                ledAndVoiceMsg.setDataLength(length);
                EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
                ehcacheUtil.put(Constant.PARK_CACHE, "led_voice_" + deviceNo, ledAndVoiceMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("【语音下发】线程异常", e);
        }
    }

    private List<LedConfigBean> covertLedConfigBean(List<LedConfig> listTmp) {
        List<LedConfigBean> list = new ArrayList<LedConfigBean>();
        for (LedConfig ledConfig : listTmp) {
            LedConfigBean bean = new LedConfigBean();
            bean.setText(ledConfig.getText());
            bean.setDelayTime(ledConfig.getDelayTime());
            bean.setDisMode(ledConfig.getDisMode());
            bean.setDisplayNum(ledConfig.getDisplayNum());
            bean.setEnterSpeed(ledConfig.getEnterSpeed());
            bean.setId(ledConfig.getId());
            bean.setLineId(ledConfig.getLineId());
            bean.setTextColor(ledConfig.getTextColor());
            bean.setType(ledConfig.getType());
            list.add(bean);
        }
        return list;
    }

    /**
     * 根据行号获取模板
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/30 16:48
     **/
    private LedConfigBean getLineBynum(List<LedConfigBean> list, int lineNum) {
        LedConfigBean ledConfig1 = null;
        for (LedConfigBean ledConfig : list) {
            if (ledConfig.getLineId() == lineNum) {
                ledConfig1 = ledConfig;
                break;
            }
        }
        return ledConfig1;
    }

    /**
     * 获取约租车的剩余天数
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/4 10:05
     **/
    private int getRemainDays(Date date1, Date Date2) {
        int remain_days = 0;
        DateTime t1 = new DateTime(date1);
        DateTime t2 = new DateTime(Date2);
        remain_days = Days.daysBetween(t2.withTimeAtStartOfDay(), t1.withTimeAtStartOfDay()).getDays();
        remain_days++;
        return remain_days;
    }

}
