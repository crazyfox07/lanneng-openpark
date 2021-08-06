package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.ParkInnerCars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ParkInnerCarsRepository extends JpaRepository<ParkInnerCars, String> {

    /**
     * 根据车牌号码和车辆类型查询在场车辆
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 16:44
     **/
    @Query(value = "select t from ParkInnerCars t where t.carno = :plateNo and t.plateType = :PlateType ")
    List<ParkInnerCars> findParInnerCars(@Param(value = "plateNo") String plateNo, @Param(value = "PlateType") String PlateType);

    /**
     * 删除所有数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:31
     **/
    @Transactional
    @Modifying
    @Query(" delete from ParkInnerCars t where 1=1 ")
    int deleteParkInnerCarsAll();
}
