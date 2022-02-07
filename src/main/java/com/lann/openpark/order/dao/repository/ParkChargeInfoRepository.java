package com.lann.openpark.order.dao.repository;

import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


public interface ParkChargeInfoRepository extends JpaRepository<ParkChargeInfo, String> {
    /**
     * 根据车牌号码和车牌类型查询订单信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 10:55
     **/
    @Query(value = "select t from ParkChargeInfo t where t.carno = :plateNo and t.plateType = :PlateType and t.exitType in('0','7')  order by t.collectiondate1 desc")
    List<ParkChargeInfo> findOutCarOrder(@Param(value = "plateNo") String plateNo, @Param(value = "PlateType") String PlateType);


    /**
     * 根据nid号查询订单信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 15:38
     **/
    @Query(value = " select t from ParkChargeInfo t where t.nid = :nid ")
    ParkChargeInfo findParkChargeInfoByNid(@Param(value = "nid") String nid);

    @Query(value = "select t from ParkChargeInfo t where t.carno = :carno and  t.collectiondate1 >= :collectiondate1 order by t.collectiondate1 desc")
    List<ParkChargeInfo> findParkChargeInfoByPlateNo(@Param(value = "carno") String carno, @Param(value = "collectiondate1") Date collectiondate1);

    /**
     * 查询车牌号未结束的订单
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 10:29
     **/
    @Query(value = " select t from ParkChargeInfo t where t.carno = :plateNo and t.licensetype = :licensetype and t.exitType in('0','7') order by t.collectiondate1 desc ")
    List<ParkChargeInfo> findParkChargeInfoUnclosed(@Param(value = "plateNo") String plateNo, @Param(value = "licensetype") String licensetype);

    /**
     * 查询需要上传的驶入记录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/17 17:49
     **/
    @Query(value = "select t from ParkChargeInfo t where t.inRandom = :inRandom")
    List<ParkChargeInfo> findOrderInByRandom(@Param(value = "inRandom") String inRandom);

    /**
     * 查询需要上传的驶离记录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/17 17:51
     **/
    @Query(value = "select t from ParkChargeInfo t where t.outRandom = :outRandom")
    List<ParkChargeInfo> findOrderOutByRandom(@Param(value = "outRandom") String outRandom);

    /**
     * 查询需要上传的支付记录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/17 17:51
     **/
    @Query(value = "select t from ParkChargeInfo t where t.payRandom = :payRandom")
    List<ParkChargeInfo> findOrderPayByRandom(@Param(value = "payRandom") String payRandom);

    /**
     * 删除指定日期之前的数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 15:58
     **/
    @Transactional
    @Modifying
    @Query("delete from ParkChargeInfo t where t.collectiondate1 < :delDate")
    int delParkChargeInfoByDate(@org.springframework.data.repository.query.Param(value = "delDate") Date delDate);

    /**
     * 清空所有数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:36
     **/
    @Transactional
    @Modifying
    @Query("delete from ParkChargeInfo t where 1=1 ")
    int delParkChargeInfoAll();

    /**
     * 查询当日该车订单
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/2 9:47
     **/
    @Query(value = " select t from ParkChargeInfo t where t.carno = :plateNo and t.plateType = :plateType and t.collectiondate1 >= :today ")
    List<ParkChargeInfo> findCarOrderDay(@Param(value = "plateNo") String plateNo, @Param(value = "plateType") String plateType, @Param(value = "today") Date today);

    /**
     * 查询所有场内车
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 11:33
     **/
    @Query(value = " select t from ParkChargeInfo t where t.exitType in('0','7') and t.collectiondate1 >= :startDate and t.collectiondate1 <= :endDate ")
    List<ParkChargeInfo> findAllUnclosedOrder(@Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);

    /**
     * 根据停车场区域查询未结束订单
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/21 10:14
     **/
    @Query(value = " select t from ParkChargeInfo t, EquipInfo t1, GateInfo t2, ParkRegionInfo t3 where t.devicecode = t1.devicecode and t1.pointcode = t2.pointcode and t2.regionCode = t3.regionCode " +
            " and t2.regionCode=:regionCode and t.exitType in('0','7') and t.collectiondate1 >= :startDate and t.collectiondate1 <= :endDate ")
    List<ParkChargeInfo> findAllUnclosedOrderByRegionCode(@Param(value = "regionCode") String regionCode, @Param(value = "startDate") Date startDate, @Param(value = "endDate") Date endDate);

    /**
     * 查询指定时间内所有已经结束的订单
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/29 9:30
     **/
    @Query(value = " select t from ParkChargeInfo t where t.exitType not in('0','7') and t.collectiondate1 >= :start and t.collectiondate1 <= :end ")
    List<ParkChargeInfo> findOrderByTime(@Param(value = "start") Date start, @Param(value = "end") Date end);

}
