package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.EquipCometMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface EquipCometMsgRepository extends JpaRepository<EquipCometMsg,String> {
    /**
     * 根据设备编号查询comet轮询消息
     * @Author songqiang
     * @Description 
     * @Date 2020/7/8 8:57
    **/
    @Query(value = "select t from EquipCometMsg t where t.devicecode = :devicecode ")
    EquipCometMsg findEquipCometMsgByDevicecode(@Param(value = "devicecode") String devicecode);
}
