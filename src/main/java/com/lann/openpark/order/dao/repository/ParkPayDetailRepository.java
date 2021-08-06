package com.lann.openpark.order.dao.repository;

import com.lann.openpark.order.dao.entiy.ParkPayDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface ParkPayDetailRepository extends JpaRepository<ParkPayDetail, String> {

    /**
     * 清空所有数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:36
     **/
    @Transactional
    @Modifying
    @Query("delete from ParkPayDetail t where 1=1 ")
    int delParkPayDetailAll();


}
