package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ParkUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ParkUserInfoRepository extends JpaRepository<ParkUserInfo, String> {
    /**
     * 根据用户名查询用户信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 9:34
     **/
    @Query(value = "select t from ParkUserInfo t where t.userid = :userName ")
    ParkUserInfo findParkUserInfoByUserid(@Param(value = "userName") String userName);

    /**
     * 查询用户列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 9:20
     **/
    @Query(value = "select t from ParkUserInfo t ")
    List<ParkUserInfo> findParkUserInfoList();

}
