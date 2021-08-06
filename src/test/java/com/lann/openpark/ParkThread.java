package com.lann.openpark;

import com.lann.openpark.park.service.ParkService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParkThread implements Runnable {

    private int direction;
    private int delay;
    ParkService parkService;

    public ParkThread(ParkService parkService, int direction, int delay) {
        this.parkService = parkService;
        this.direction = direction;
        this.delay = delay;
    }

    public void run() {
        try {
            Thread.sleep(delay);// 模拟停车时间
            if (9 == direction) {// 进场
                log.info("车辆进场....");
                parkService.updateBerthCount(-1, "11");
            } else {// 出场
                log.info("车辆出场....");
                parkService.updateBerthCount(1, "11");
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("测试车辆入场出场线程出错");
        }
    }


}
