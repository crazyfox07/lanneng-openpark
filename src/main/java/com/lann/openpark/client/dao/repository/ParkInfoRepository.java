package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ParkInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ParkInfoRepository extends JpaRepository<ParkInfo, String> {

    /**
     * 根据停车场编号获取停车场信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 14:20
     **/
    @Query(value = "select t from ParkInfo t where t.parkcode = :parkCode ")
    ParkInfo findParkInfoById(@Param(value = "parkCode") String parkCode);

}
