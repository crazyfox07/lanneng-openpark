package com.lann.openpark.client.threads;

import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.entiy.GateMsg;
import com.lann.openpark.camera.threads.VoiceLedThread;
import com.lann.openpark.charge.util.FileOperateUtil;
import com.lann.openpark.client.dao.entiy.LogPoleControl;
import com.lann.openpark.client.dao.repository.LogPoleControlRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.util.ApplicationContextUtil;
import com.lann.openpark.util.EhcacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.util.List;
import java.util.Properties;

public class GetPicThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(VoiceLedThread.class);

    private LogPoleControlRepository logPoleControlRepository = ApplicationContextUtil.getBean(LogPoleControlRepository.class);

    private String deviceCode;
    private LogPoleControl logPoleControl;

    public GetPicThread(String deviceCode, LogPoleControl logPoleControl) {
        this.deviceCode = deviceCode;
        this.logPoleControl = logPoleControl;
    }


    public void run() {
        try {
            // 缓存
            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            GateMsg gateMsg1 = new GateMsg();
            gateMsg1.setDevicecode(deviceCode);
            ehcacheUtil.put(Constant.PARK_CACHE, "manual_trigger_" + deviceCode, gateMsg1);
            CameraData cameraData = null;
            Thread.sleep(3000);
            for (int i = 0; i < 5; i++) {
                log.info("第 " + (i + 1) + " 次读取缓存...");
                // 从缓存中获取cameraData的ID号
                cameraData = (CameraData) ehcacheUtil.get(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                if (null != cameraData) {
                    // 删除缓存
                    log.info("读取到识别ID: " + cameraData.getId());
                    ehcacheUtil.remove(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                    break;
                } else {
                    Thread.sleep(3000);
                }
            }

            if (null == cameraData) {
                this.log.error("抓图线程未抓取到图片！");
            } else {
                // 存照片
                String imgPath = this.uploadImgOne(cameraData);
                // 更新bean
                logPoleControl.setImgPath(Constant.IMG_SERVER + imgPath);
                logPoleControlRepository.save(logPoleControl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("【停车场相机抓图】线程异常", e);
        }
    }


    public String uploadImgOne(CameraData cameraData) {

        // 从缓存中获取图片上传路径、后缀信息
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String fileRoot = properties.getProperty("file_upload_root");
        // 判断改路径是否携带"\"，若没有，则添加
        if (!fileRoot.endsWith("\\")) {
            fileRoot = fileRoot + "\\";
        }
        String suffix = properties.getProperty("file_upload_suffix");

        String imgPath = "";
        try {
            if (null != cameraData.getImageFile()) {
                String imgBase64Str = Base64Utils.encodeToString(cameraData.getImageFile());
                String[] images = new String[1];
                images[0] = imgBase64Str;
                List<String> imageNames = FileOperateUtil.saveThirdParkBase64Images(fileRoot, images, suffix, "pole");
                if (imageNames.size() > 0) {
                    // log.info("【驶入/驶离】--上传图片文件返回路径：" + imageNames.get(0));
                    imgPath = imageNames.get(0);
                }
            }
        } catch (Exception e) {
            log.error("【驶入驶离】---图片上传失败。");
            e.printStackTrace();
            return "";
        }
        return imgPath.replaceAll("\\\\", "/");
    }

}
