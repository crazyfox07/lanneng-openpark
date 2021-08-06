package com.lann.openpark.util;

import com.lann.openpark.camera.bean.LedConfigBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceLedUtil {

    private static final Logger log = LoggerFactory.getLogger(VoiceLedUtil.class);

    private static final int LED_COLOR_RED = 255; //红色
    private static final int LED_COLOR_GREEN = 65280; //绿色
    private static final int LED_COLOR_YEELOW = 65535; //黄色
    private static final int LED_COLOR_BLUE = 16711680; //绿色

    public static void main(String[] args) {
    }

    /**
     * 根据输入内容获得可以播报的base64串
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/27 17:14
     **/
    public static Map getVoiceStr(String msg, byte opt) {
        try {
            byte[] bt2 = msg.getBytes("GB2312");
            byte[] bt1 = new byte[]{0x00, 0x64, (byte) 0xFF, (byte) 0xFF, 0x30, (byte) (bt2.length + 1), opt};
            byte[] bt3 = new byte[bt1.length + bt2.length];
            // 将bt1和bt2合并为bt3
            System.arraycopy(bt1, 0, bt3, 0, bt1.length);
            System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
            // 获得CRC校验码
            byte[] bt4 = CRC16Util.getCrc16(bt3);
            byte[] bt5 = new byte[bt3.length + bt4.length];
            // 将bt3和bt4合并为bt5
            System.arraycopy(bt3, 0, bt5, 0, bt3.length);
            System.arraycopy(bt4, 0, bt5, bt3.length, bt4.length);
//            log.info(bt5.length + "");
//            log.info(HexUtil.bytesToHexString(bt5).toUpperCase());
//            log.info(Base64Util.byteArrToBase64(bt5));
            Map map = new HashMap();
            map.put("length", bt5.length);
            map.put("baseStr", Base64Util.byteArrToBase64(bt5));
            return map;
        } catch (Exception e) {
            log.error("【语音播报】转换失败！" + e.getMessage());
            Map map = new HashMap();
            map.put("length", -1);
            map.put("baseStr", "");
            return map;
        }
    }

    /**
     * 获取更新时间的base64串
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/4 11:20
     **/
    public static Map getUpTimeStr(Date date) {
        try {
            DateTime now = new DateTime(new Date());

            int year = now.getYear();
            byte month = (byte) now.getMonthOfYear();
            byte day = (byte) now.getDayOfMonth();
            int week = now.getDayOfWeek();
            byte weekByte = (byte) 1;
            // 星期处理
            if (week != 7) {
                weekByte = (byte) (now.getDayOfWeek() + 1);
            }
            byte hour = (byte) now.getHourOfDay();
            byte mintue = (byte) now.getMinuteOfHour();
            byte second = (byte) now.getSecondOfMinute();

            int pos = 0;
            byte[] bt1 = new byte[512];
            bt1[pos++] = 0x00; //显示屏地址
            bt1[pos++] = 0x64; //固定参数
            bt1[pos++] = (byte) 0xFF; //包序列
            bt1[pos++] = (byte) 0xFF; //包序列
            bt1[pos++] = 0x05; //指令
            bt1[pos++] = 8; //数据长度
            bt1[pos++] = (byte) (year & 0xff);//年份低字节
            bt1[pos++] = (byte) ((year >> 8) & 0xff);//年份高字节
            bt1[pos++] = month; //月份
            bt1[pos++] = day; //日
            bt1[pos++] = weekByte; //星期
            bt1[pos++] = hour; //小时
            bt1[pos++] = mintue; //分钟
            bt1[pos++] = second; //秒钟

            // 新建一个定长数组复制数据
            byte[] bt4 = new byte[pos];
            for (int i = 0; i < pos; i++) {
                bt4[i] = bt1[i];
            }

            // 计算校验码
            byte[] bt2 = CRC16Util.getCrc16(bt4);
            byte[] bt3 = new byte[bt1.length + bt2.length];
            // 将bt1和bt2合并为bt3
            System.arraycopy(bt4, 0, bt3, 0, bt4.length);
            System.arraycopy(bt2, 0, bt3, bt4.length, bt2.length);

            Map map = new HashMap();
            map.put("length", bt3.length);
            map.put("baseStr", Base64Util.byteArrToBase64(bt3));
            return map;

        } catch (Exception e) {
            log.error("【更新时间base64】转换失败！" + e.getMessage());
            Map map = new HashMap();
            map.put("length", -1);
            map.put("baseStr", "");
            return map;
        }
    }

    /**
     * 获取LED显示的base64字符串
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/30 9:35
     **/
    public static Map getLedText(byte lineNum, String text, byte disMode, byte enterSpeed, byte delayTime, int textColor, byte disTimes) {
        try {
            // 获取需要显示的内容的GB2312编码
            byte[] bt2 = text.getBytes("GB2312");//
            int length = bt2.length;
            byte[] bt1 = new byte[]{0x00, 0x64, (byte) 0xFF, (byte) 0xFF, 0x62,
                    (byte) (19 + length), lineNum, disMode, enterSpeed, 0x00,
                    delayTime, disMode, 0x01, 0x03, disTimes, (byte) (textColor & 0xff),
                    (byte) ((textColor >> 8) & 0xff), (byte) ((textColor >> 16) & 0xff),
                    (byte) ((textColor >> 24) & 0xff), 0x00, 0x00, 0x00, 0x00, (byte) length, 0x00};
            byte[] bt3 = new byte[bt1.length + bt2.length];
            // 将bt1和bt2合并为bt3
            System.arraycopy(bt1, 0, bt3, 0, bt1.length);
            System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
            // 获得CRC校验码
            byte[] bt4 = CRC16Util.getCrc16(bt3);
            byte[] bt5 = new byte[bt3.length + bt4.length];
            // 将bt3和bt4合并为bt5
            System.arraycopy(bt3, 0, bt5, 0, bt3.length);
            System.arraycopy(bt4, 0, bt5, bt3.length, bt4.length);
            log.info(bt5.length + "");
            log.info(HexUtil.bytesToHexString(bt5).toUpperCase());
            log.info(Base64Util.byteArrToBase64(bt5));
            Map map = new HashMap();
            map.put("length", bt5.length);
            map.put("baseStr", Base64Util.byteArrToBase64(bt5));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【LED显示】转换失败！" + e.getMessage());
            Map map = new HashMap();
            map.put("length", -1);
            map.put("baseStr", "");
            return map;
        }
    }

    /**
     * LED和语音播报同时进行
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/30 17:28
     **/
    public static Map getVoicrAndLedStr(List<LedConfigBean> list_led, String voiceText, int saveFlag) {
        try {
            // byte[] bt1 = new byte[]{0x00, 0x64, (byte) 0xFF, (byte) 0xFF, (byte) 0x6E, 0, (byte) saveFlag, (byte) list_led.size()};
            // int pos = bt1.length;
            int pos = 0;
            byte[] bt1 = new byte[253];
            bt1[pos++] = 0x00;
            bt1[pos++] = 0x64;
            bt1[pos++] = (byte) 0xFF;
            bt1[pos++] = (byte) 0xFF;
            bt1[pos++] = (byte) 0x6E;
            bt1[pos++] = (byte) 0;
            bt1[pos++] = (byte) saveFlag;
            bt1[pos++] = (byte) list_led.size();
            for (int i = 0; i < list_led.size(); i++) {
                LedConfigBean ledConfig = list_led.get(i);
                bt1[pos++] = (byte) ledConfig.getLineId();// 行号
                bt1[pos++] = (byte) ledConfig.getDisMode();// 显示模式
                bt1[pos++] = (byte) ledConfig.getEnterSpeed();// 进入速度
                bt1[pos++] = (byte) ledConfig.getDelayTime();// 停留时间
                bt1[pos++] = (byte) ledConfig.getDisplayNum();// 显示次数
                bt1[pos++] = (byte) (ledConfig.getTextColor() & 0xff); // 32位字体颜色 红色分量
                bt1[pos++] = (byte) ((ledConfig.getTextColor() >> 8) & 0xff);// 32位字体颜色 绿色分量
                bt1[pos++] = (byte) ((ledConfig.getTextColor() >> 16) & 0xff);// 32位字体颜色 蓝色分量
                bt1[pos++] = (byte) ((ledConfig.getTextColor() >> 24) & 0xff);// 32位字体颜色 保留字节
                byte[] textBuff = ledConfig.getText().getBytes("GB2312");//
                bt1[pos++] = (byte) textBuff.length;// 文本长度
                for (int j = 0; j < textBuff.length; j++) {
                    bt1[pos++] = textBuff[j];
                }
                if (i == (list_led.size() - 1)) {
                    bt1[pos++] = 0x00;
                } else {
                    bt1[pos++] = 0x0D;
                }
            }
            byte[] voiceBuff = voiceText.getBytes("GB2312");//
            if (voiceBuff.length > 0) {
                bt1[pos++] = 0x0A;// 语音分隔符
                bt1[pos++] = (byte) voiceBuff.length;// 语音文本长度
                for (int i = 0; i < voiceBuff.length; i++) {// 复制文本到缓冲
                    bt1[pos++] = voiceBuff[i];
                }
            } else {
                bt1[pos++] = 0x00;
            }

            bt1[pos++] = 0;
            bt1[5] = (byte) (pos - 6); //重新修改数据长度

            // 新建一个定长数组复制数据
            byte[] bt4 = new byte[pos];
            for (int i = 0; i < pos; i++) {
                bt4[i] = bt1[i];
            }

            // 计算校验码
            byte[] bt2 = CRC16Util.getCrc16(bt4);
            byte[] bt3 = new byte[bt4.length + bt2.length];
            // 将bt3和bt4合并为bt5
            System.arraycopy(bt4, 0, bt3, 0, bt4.length);
            System.arraycopy(bt2, 0, bt3, bt4.length, bt2.length);
            // log.info(bt3.length + "");
            // log.info(HexUtil.bytesToHexString(bt3).toUpperCase());
            // log.info(Base64Util.byteArrToBase64(bt3));
            Map map = new HashMap();
            map.put("length", bt3.length);
            map.put("baseStr", Base64Util.byteArrToBase64(bt3));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("【LED显示和语音播报】转换失败！" + e.getMessage());
            Map map = new HashMap();
            map.put("length", -1);
            map.put("baseStr", "");
            return map;
        }
    }
}
