package com.lann.openpark.charge.dao.repository;

import com.lann.openpark.charge.dao.entiy.ChargePlanConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ChargePlanConfigRepository extends JpaRepository<ChargePlanConfig, String> {
    @Transactional
    @Modifying
    @Query("delete from ChargePlanConfig t where t.planId = :planId")
    int deletePlanConfigByPlanId(@Param(value = "planId") String planId);
}
