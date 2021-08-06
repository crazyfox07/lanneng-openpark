package com.lann.openpark.charge.dao.repository;

import com.lann.openpark.charge.dao.entiy.ChargePlanRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ChargePlanRegionRepository extends JpaRepository<ChargePlanRegion, String> {

    @Transactional
    @Modifying
    @Query("delete from ChargePlanRegion t where t.planId = :planId")
    int deletePlanRegionByPlanId(@Param(value = "planId") String planId);

}
