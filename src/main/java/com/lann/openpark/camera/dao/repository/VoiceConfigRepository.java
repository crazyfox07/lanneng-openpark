package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.VoiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface VoiceConfigRepository extends JpaRepository<VoiceConfig, String> {

    /**
     * 根据类型查询语音播报信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/30 16:23
     **/
    @Query(value = "select t from VoiceConfig t where t.type = :type ")
    VoiceConfig findVoiceConfigByType(@Param(value = "type") int type);
}
