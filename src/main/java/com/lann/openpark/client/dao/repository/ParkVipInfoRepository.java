package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ParkVipInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ParkVipInfoRepository extends JpaRepository<ParkVipInfo, String> {

    @Query(value = " select t from ParkVipInfo t where t.vipId = :vipId ")
    ParkVipInfo findGateInfoByVipId(@Param(value = "vipId") String vipId);

    /**
     * 根据车牌号查询会员信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 15:33
     **/
    @Query(value = " select t from ParkVipInfo t where t.carno = :carno and t.licenceType = :licenceType")
    ParkVipInfo findGateInfoByCarno(@Param(value = "carno") String carno, @Param(value = "licenceType") String licenceType);

    /**
     * 查询所有会员列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/16 9:33
     **/
    @Query(value = " select t from ParkVipInfo t ")
    List<ParkVipInfo> queryLeaguerAll();
}
