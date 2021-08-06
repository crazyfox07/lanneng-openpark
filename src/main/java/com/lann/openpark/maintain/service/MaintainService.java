package com.lann.openpark.maintain.service;

import com.lann.openpark.camera.dao.repository.CameraDataRepository;
import com.lann.openpark.camera.dao.repository.ParkDetectInfoRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 停车场维护服务
 *
 * @Author songqiang
 * @Description
 * @Date 2020/9/10 10:07
 **/
@Service
public class MaintainService {
    private static final Logger log = LoggerFactory.getLogger(MaintainService.class);

    public static void main(String[] args) {
        String test = "20200807";
        log.info(test.substring(0, 4) + "-" + test.substring(4, 6) + "-" + test.substring(6));
    }

    @Autowired
    CameraDataRepository cameraDataRepository;
    @Autowired
    ParkDetectInfoRepository parkDetectInfoRepository;
    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;

    /**
     * 停车场数据删除（图片和cameraData）
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 10:12
     **/
    public void deleteParkData() {
        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String parkDataExpDays = properties.getProperty("park_data_exp_days");
        String fileRoot = properties.getProperty("file_upload_root");// 上传文件路径

        if (-1 == Integer.valueOf(parkDataExpDays)) {// 标识不删除
            return;
        }

        Date now = new Date();
        Date last_date = DateUtil.addDay(now, -Integer.valueOf(parkDataExpDays));// 删除日期最后一天
        log.info(DateUtil.formatDateYMDHMS(last_date) + "之前日期的停车数据将被删除");
        // 获取图片目录下所有的图片文件夹
        List<String> list_img = FileUtil.getAllFolderNames(fileRoot + "parkImgs");
        // 删除停车图片
        for (String dateStrTmp : list_img) {
            try {
                // 将文件夹名转换成日期
                Date dateTmp = DateUtil.parseDateYMDHMS(dateStrTmp.substring(0, 4) + "-" + dateStrTmp.substring(4, 6) + "-" + dateStrTmp.substring(6) + " 00:00:00");
                if (dateTmp.before(last_date)) {// 这个日期再需要删除的最后日期之前，删除文件夹
                    FileUtil.deleteDirs(fileRoot + "parkImgs\\" + dateStrTmp);
                }
            } catch (Exception e) {
                continue;
            }
        }

        // 删除cameraData，三天前的数据，没什么用
        cameraDataRepository.delCameraDataByDate(DateUtil.addDay(now, -3));
        // 删除parkDetectInfo
        parkDetectInfoRepository.delParkDetectInfoByDate(last_date);
        // 删除parkChargeInfo
        parkChargeInfoRepository.delParkChargeInfoByDate(last_date);
    }
}
