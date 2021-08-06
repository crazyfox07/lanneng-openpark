package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.BlackInfo;
import com.lann.openpark.client.dao.entiy.ParkVipInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BlackInfoRepository extends JpaRepository<BlackInfo, String> {

    /**
     * 根据ID查询黑名单
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 11:10
     **/
    @Query(value = " select t from BlackInfo t where t.blackId = :blackId ")
    BlackInfo findBlackInfoByVipId(@Param(value = "blackId") String blackId);

    /**
     * 根据车牌号查询黑名单
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 11:10
     **/
    @Query(value = " select t from BlackInfo t where t.carno = :carno and t.carnoType = :licenceType")
    ParkVipInfo findBlackInfoByCarno(@Param(value = "carno") String carno, @Param(value = "licenceType") String licenceType);

    /**
     * 查询所有黑名单列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/16 9:33
     **/
    @Query(value = " select t from BlackInfo t ")
    List<BlackInfo> queryBlackAll();
}
