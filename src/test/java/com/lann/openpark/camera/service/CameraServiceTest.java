package com.lann.openpark.camera.service;

import com.lann.openpark.OpenparkApplicationTests;
import com.lann.openpark.camera.bean.ParkVipBean;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CameraServiceTest extends OpenparkApplicationTests {

    @Autowired
    CameraService cameraService;

    @Test
    void getVipInfo() {
        ParkVipBean vip = cameraService.getVipInfo("鲁L12345", "1");
        Assert.assertNull(vip);
    }
}