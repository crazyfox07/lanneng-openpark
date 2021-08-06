package com.lann.openpark.task;

import com.lann.openpark.camera.dao.entiy.LedBaseShow;
import com.lann.openpark.camera.dao.repository.LedBaseShowRepository;
import com.lann.openpark.camera.service.CameraService;
import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import com.lann.openpark.client.dao.repository.ParkRegionInfoRepository;
import com.lann.openpark.foreign.web.ForeignController;
import com.lann.openpark.maintain.service.MaintainService;
import com.lann.openpark.openepark.service.OpenEparkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TimerTask {


    @Autowired
    OpenEparkService openEparkService;
    @Autowired
    CameraService cameraService;
    @Autowired
    MaintainService maintainService;
    @Autowired
    LedBaseShowRepository ledBaseShowRepository;
    @Autowired
    ParkRegionInfoRepository parkRegionInfoRepository;
    @Autowired
    ForeignController foreignController;

    @Value("${parkInfo.isEpark}")
    boolean isEpark;// 是否对接好停车

    /**
     * 任务1和任务2是并发执行的，因为在config中配置了AsyncTaskConfig
     * AsyncTaskConfig初始化了线程池
     * private int corePoolSize = 5;可修改线程并发数
     */


    // @Scheduled(cron="0/3 * * * * *")// cron表达式，每隔三秒执行一次
    public void testTask() throws InterruptedException {
        System.out.println("任务1开始执行" + new Date());
        Thread.sleep(5000);
        System.out.println("任务1结束执行" + new Date());
    }

    // @Scheduled(cron="0/3 * * * * *")// cron表达式，每隔三秒执行一次
    public void testTask1() throws InterruptedException {
        System.out.println("任务2开始执行" + new Date());
        Thread.sleep(5000);
        System.out.println("任务2结束执行" + new Date());
    }

    /**
     * 泊位数据上传
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/9 16:17
     **/
    @Scheduled(cron = "0 0/3 * * * *")// cron表达式，每3分钟执行一次
    public void uploadSpaceInfo() {
        if (isEpark) {
            openEparkService.uploadSpaceInfo();
        }
    }

    /**
     * 屏显更新定时任务
     *
     * @Author songqiang
     * @Description
     * @Date 2021/8/4 9:09
     **/
    @Scheduled(cron = "0/30 * * * * *")// cron表达式，每30秒执行一次
    public void updateLedDisplay() {
        // log.info("更新屏显定时器开始：" + DateUtil.formatDateYMDHMS(new Date()));
        // 查询所有需要更新的数据
        List<LedBaseShow> list = ledBaseShowRepository.findAll();
        if (list.size() == 0) return;
        for (LedBaseShow ledBaseShow : list) {
            String text = ledBaseShow.getText();// 需要显示的内容
            switch (ledBaseShow.getFunType()) {// 根据功能不同，下发不通
                case 1:// 余位数显示
                    ParkRegionInfo region = parkRegionInfoRepository.findRegionInfoByDeviceCode(ledBaseShow.getCameraId());// 根据设备号查询余位信息
                    if (null == region) return;
                    int remianBerths = region.getRegionRectifyCount();// 剩余车位数
                    text = text.replace("$(remainBerths)", remianBerths + "");// 替换显示
                    break;
                default:
                    break;
            }
            // 调用接口下发显示
            foreignController.updateLedDisplay(ledBaseShow.getLineNum(), text, ledBaseShow.getDisMode(), ledBaseShow.getEnterSpeed(),
                    ledBaseShow.getDelayTime(), ledBaseShow.getTextColor(), ledBaseShow.getDisTimes(), ledBaseShow.getCameraId());
        }
    }

    /**
     * 更新设备时间
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/4 11:15
     **/
    // @Scheduled(cron = "0 0/1 * * * *")// cron表达式，每分钟执行一次
    @Scheduled(cron = "0 0 1 * * *")// cron表达式，每天晚上1点执行一次
    public void updateDeviceTime() {
        if (isEpark) {
            cameraService.updateDeviceTime();
        }
    }

    // 停车场数据删除任务
    @Scheduled(cron = "0 0 3 * * *")// cron表达式，每天晚上3点执行一次
    public void deleParkData() {
        if (isEpark) {
            maintainService.deleteParkData();
        }
    }

}
