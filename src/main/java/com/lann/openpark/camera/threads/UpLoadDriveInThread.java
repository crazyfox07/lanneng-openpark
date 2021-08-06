package com.lann.openpark.camera.threads;

import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpLoadDriveInThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(VoiceLedThread.class);

    private OpenEparkService openEparkService = ApplicationContextUtil.getBean(OpenEparkService.class);
    private ParkChargeInfo parkChargeInfo;
    private CameraData cameraData;

    /**
     * 构造函数
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:43
     **/
    public UpLoadDriveInThread(ParkChargeInfo parkChargeInfo, CameraData cameraData) {
        this.parkChargeInfo = parkChargeInfo;
        this.cameraData = cameraData;
    }

    /**
     * 线程运行方法
     * 上传驶入数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:43
     **/
    public void run() {
        try {
            // 执行上传
            openEparkService.uploadDriveIn(parkChargeInfo, cameraData);
            log.info("【驶入数据上传】ID=" + parkChargeInfo.getEnterNid());
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("【执行驶入数据上传】线程异常", e);
        }
    }

}
