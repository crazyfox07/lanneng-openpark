package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.GateInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface GateInfoRepository extends JpaRepository<GateInfo, String> {

    /**
     * 查询所有gate信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:51
     **/
    @Query(value = "select t from GateInfo t")
    List<GateInfo> findGateInfoList();

    /**
     * 根据区域信息查询出口信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:43
     **/
    @Query(value = "select t from GateInfo t where t.regionCode = :regionCode ")
    List<GateInfo> findGateInfoByRegionCode(@Param(value = "regionCode") String regionCode);

    @Query(value = " select t from GateInfo t where t.pointcode = :pointCode ")
    GateInfo findGateInfoByPoineCode(@Param(value = "pointCode") String pointCode);
    

    // 更新gate的停车场编号
    @Transactional
    @Modifying
    @Query("update GateInfo t set t.parkcode = :parkCode ")
    int updateGateInfoAll(@Param(value = "parkCode") String parkCode);

}
