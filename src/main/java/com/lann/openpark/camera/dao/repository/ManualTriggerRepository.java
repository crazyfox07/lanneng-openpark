package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.ManualTrigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ManualTriggerRepository extends JpaRepository<ManualTrigger,String> {
    
    /**
     * 更新需要回传的识别结果
     * @Author songqiang
     * @Description 
     * @Date 2020/7/13 16:45
    **/
    @Transactional
    @Modifying
    @Query("update ManualTrigger t set t.random = :randomNum where t.devicecode = :devicecode and t.random is null ")
    public int updateManualTriggerByRandom(@Param(value = "randomNum") String randomNum, @Param(value = "devicecode") String devicecode);

    /**
     * 根据随机数查询需要回传的是被结果
     * @Author songqiang
     * @Description 
     * @Date 2020/7/13 16:46
    **/
    @Query(value = " select t from ManualTrigger t where t.random = :randomNum")
    List<ManualTrigger> findManualTriggerByRandom(@Param(value = "randomNum") String randomNum);
}
