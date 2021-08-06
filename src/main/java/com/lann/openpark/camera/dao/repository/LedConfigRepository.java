package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.LedConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface LedConfigRepository extends JpaRepository<LedConfig, String> {

    /**
     * 根据类型查询led显示信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/30 16:23
     **/
    @Query(value = "select t from LedConfig t where t.type = :type order by t.lineId ")
    List<LedConfig> findLedConfigByType(@Param(value = "type") int type);

}
