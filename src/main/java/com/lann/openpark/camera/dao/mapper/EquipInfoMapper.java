package com.lann.openpark.camera.dao.mapper;

import com.lann.openpark.camera.bean.EquipBean;
import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.client.bean.EquipGateBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface EquipInfoMapper {
    // 根据设备编号查询设备和出入口信息
    List<EquipGateInfo> findEquipAndGateInfo(@Param("devicecode") String devicecode);

    // 查询所有设备信息
    List<EquipBean> findEquipList();

    // 查询所有相机信息
    List<EquipGateBean> findEquipGateList();

    // 根据出入口信息查询相机信息
    List<EquipBean> findEquipByPointCode(@Param("pointCode") String pointCode);

    // 删除相机信息
    int deleteEquipByDeviceCode(@Param("pointCode") String pointCode);
}
