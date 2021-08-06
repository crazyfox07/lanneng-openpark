package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.EquipSendMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface EquipSendMsgRepository extends JpaRepository<EquipSendMsg,String> {
    /**
     * 设备需要下发的消息
     * @Author songqiang
     * @Description 
     * @Date 2020/7/8 8:57
    **/
    @Query(value = " select t from EquipSendMsg t where t.devicecode = :devicecode and t.msgType = :msgType ")
    List<EquipSendMsg> findEquipSendMsgByDevicecode(@Param(value = "devicecode") String devicecode, @Param(value = "msgType") int msgType);
    
    /**
     * 更新下发消息随机数(单条数据)
     * @Author songqiang
     * @Description 
     * @Date 2020/7/8 9:30s
    **/
    @Modifying
    @Query("update EquipSendMsg t set t.randomNum = :randomNum where t.devicecode = :devicecode and t.msgType in (1,2) and t.randomNum is null ")
    public int updateEquipSendMsgRandom(@Param(value = "randomNum") String randomNum, @Param(value = "devicecode") String devicecode);

    /**
     * 查询待下发的消息列表
     * @Author songqiang
     * @Description 
     * @Date 2020/7/13 16:09
    **/
    @Query(value = " select t from EquipSendMsg t where t.randomNum = :randomNum")
    List<EquipSendMsg> findEquipSendMsgByRandom(@Param(value = "randomNum") String randomNum);

}
