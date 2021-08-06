package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.AbnormalOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AbnormalOutRepository extends JpaRepository<AbnormalOut, String> {
    @Query(value = " select t from AbnormalOut t where t.plateNo = :plateNo and t.plateType = :plateType ")
    AbnormalOut findAbnormalOutByCar(@Param(value = "plateNo") String plateNo, @Param(value = "plateType") String plateType);

    @Query(value = " select t from AbnormalOut t where t.id = :id ")
    AbnormalOut findByAbnormalId(@Param(value = "id") String id);

    @Query(value = " select t from AbnormalOut t where t.cumulNum <= :num ")
    List<AbnormalOut> findListByCumulNum(@Param(value = "num") int num);
}
