package com.lann.openpark.camera.threads;

import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.openepark.dao.entiy.OrderPay;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpLoadPayDetailThread implements Runnable {
    private Logger log = LoggerFactory.getLogger(VoiceLedThread.class);

    private OpenEparkService openEparkService = ApplicationContextUtil.getBean(OpenEparkService.class);

    private ParkChargeInfo parkChargeInfo;
    OrderPay orderPay;
    private CameraData cameraData;

    /**
     * 构造函数
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:43
     **/
    public UpLoadPayDetailThread(ParkChargeInfo parkChargeInfo, OrderPay orderPay, CameraData cameraData) {
        this.parkChargeInfo = parkChargeInfo;
        this.orderPay = orderPay;
        this.cameraData = cameraData;
    }

    /**
     * 线程运行方法
     * 上传支付信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/31 15:43
     **/
    public void run() {
        try {
            Thread.sleep(2000);// 线程休眠2秒
            // 执行上传
            openEparkService.uploadPayDetail(parkChargeInfo, orderPay, cameraData);
            log.info("【支付数据上传】ID=" + parkChargeInfo.getNid());
        } catch (Exception e) {
            e.printStackTrace();
            this.log.error("【执行支付数据上传】线程异常", e);
        }
    }

}
