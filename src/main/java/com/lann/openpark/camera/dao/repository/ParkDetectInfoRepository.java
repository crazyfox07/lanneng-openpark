package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.ParkDetectInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


public interface ParkDetectInfoRepository extends JpaRepository<ParkDetectInfo, String> {

    /**
     * 根据nid号，查询驶入驶离数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 14:38
     **/
    @Query(value = "select t from ParkDetectInfo t where t.nid = :nid ")
    ParkDetectInfo findParkDetectInfoByNid(@Param(value = "nid") String nid);

    /**
     * 删除指定日期之前的数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/10 15:58
     **/
    @Transactional
    @Modifying
    @Query("delete from ParkDetectInfo t where t.collectiondate < :delDate")
    int delParkDetectInfoByDate(@Param(value = "delDate") Date delDate);

    /**
     * 清空所有数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:37
     **/
    @Transactional
    @Modifying
    @Query("delete from ParkDetectInfo t where 1=1 ")
    int delParkDetectInfoAll();

}
