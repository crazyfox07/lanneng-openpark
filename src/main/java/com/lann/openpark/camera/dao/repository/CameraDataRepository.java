package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.CameraData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


public interface CameraDataRepository extends JpaRepository<CameraData, String> {

    /**
     * 根据nid号查询原始相机数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 14:41
     **/
    @Query(value = "select t from CameraData t where t.id = :nid ")
    CameraData findCameraDataByNid(@Param(value = "nid") String nid);

    /**
     * 根据设备编号查询设备最后一次识别记录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/8/19 11:09
     **/
    @Query(value = "select * from camera_data t where t.serial_no = :serialNo order by t.insert_time desc limit 1", nativeQuery = true)
    CameraData findLastCameraDataByDeviceNo(@Param(value = "serialNo") String serialNo);

    /**
     * 删除指定日期之前的数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 15:58
     **/
    @Transactional
    @Modifying
    @Query("delete from CameraData t where t.insertTime < :delDate")
    int delCameraDataByDate(@Param(value = "delDate") Date delDate);

    /**
     * 删除所有数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:31
     **/
    @Transactional
    @Modifying
    @Query(" delete from CameraData t where 1=1 ")
    int delCameraDataAll();
}
