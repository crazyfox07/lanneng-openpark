package com.lann.openpark.charge.dao.repository;

import com.lann.openpark.charge.dao.entiy.ChargePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ChargePlanRepository extends JpaRepository<ChargePlan, String> {
    /**
     * 查询计费方案列表
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 11:54
     **/
    @Query(value = "select t from ChargePlan t ")
    List<ChargePlan> findChargePlanList();

    @Query(value = "select t from ChargePlan t where t.chargePolicyId = :nid ")
    List<ChargePlan> findChargePlanByPolicyId(@Param(value = "nid") String nid);

    @Query(value = "select t from ChargePlan t where t.nid = :nid ")
    ChargePlan findChargePlanByNid(@Param(value = "nid") String nid);
}
