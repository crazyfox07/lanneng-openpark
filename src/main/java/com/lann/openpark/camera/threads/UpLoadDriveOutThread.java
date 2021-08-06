package com.lann.openpark.camera.threads;

import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpLoadDriveOutThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(VoiceLedThread.class);

    private OpenEparkService openEparkService = ApplicationContextUtil.getBean(OpenEparkService.class);
    private ParkChargeInfo parkChargeInfo;
    private CameraData cameraData;
    private String leaveState;

    /**
     * 构造函数
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:43
     **/
    public UpLoadDriveOutThread(ParkChargeInfo parkChargeInfo, CameraData cameraData, String leaveState) {
        this.parkChargeInfo = parkChargeInfo;
        this.cameraData = cameraData;
        this.leaveState = leaveState;
    }

    /**
     * 线程运行方法
     * 上传驶离数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:43
     **/
    public void run() {
        try {
            Thread.sleep(10000);
            // 执行上传
            openEparkService.uploadDriveOut(parkChargeInfo, cameraData, leaveState);
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("【执行驶离数据上传】线程异常", e);
        }
    }

}
