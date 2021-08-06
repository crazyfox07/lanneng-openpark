package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.SysCarConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SysCarConfigRepository extends JpaRepository<SysCarConfig, String> {

    @Query(value = "select t from SysCarConfig t where t.carno = :carno and t.type = :type ")
    List<SysCarConfig> findSysCarConfig(@Param(value = "carno") String carno, @Param(value = "type") String type);

}
