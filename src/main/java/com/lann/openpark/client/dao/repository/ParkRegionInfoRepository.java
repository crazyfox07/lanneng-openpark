package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ParkRegionInfoRepository extends JpaRepository<ParkRegionInfo, String> {

    /**
     * 查询区域列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 14:28
     **/
    @Query(value = "select t from ParkRegionInfo t")
    List<ParkRegionInfo> findParkRegionInfoList();

    /**
     * 更新所有region的停车场比编号
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:52
     **/
    @Transactional
    @Modifying
    @Query("update ParkRegionInfo t set t.parkcode = :parkCode ")
    int updateParkRegionInfoAll(@Param(value = "parkCode") String parkCode);

    /**
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:40
     **/
    @Query(value = " select t from ParkRegionInfo t where t.regionCode = :regionCode ")
    ParkRegionInfo findRegionInfoByRegionCode(@Param(value = "regionCode") String regionCode);


    @Query(value = " select t2 from EquipInfo t,GateInfo t1, ParkRegionInfo t2 where t.pointcode = t1.pointcode " +
            "and t1.regionCode = t2.regionCode and t.devicecode = :deviceCode ")
    ParkRegionInfo findRegionInfoByDeviceCode(@Param(value = "deviceCode") String deviceCode);

}
