package com.lann.openpark.system.dao.repository;

import com.lann.openpark.system.dao.entiy.SysConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface SysConfigRepository extends JpaRepository<SysConfig, String> {

    /**
     * 查询数据库中所有配置信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/15 9:12
     **/
    @Query(value = "select t from SysConfig t")
    List<SysConfig> findSysConfigAll();


    @Modifying
    @Transactional
    @Query("update SysConfig s set s.config_value = :config_value where s.config_key=:config_key")
    public int updateConfigValue(@Param("config_key") String config_key, @Param("config_value") String config_value);
}
