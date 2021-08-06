package com.lann.openpark.charge.dao.repository;

import com.lann.openpark.charge.dao.entiy.Charge2Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface Charge2PolicyRepository extends JpaRepository<Charge2Policy, String> {
    @Query(value = "select t from Charge2Policy t where t.nid = :poicyId")
    Charge2Policy findChar2PolicyByNid(@Param(value = "poicyId") String poicyId);

    @Query(value = "select t from Charge2Policy t ")
    List<Charge2Policy> findChar2PolicyList();
}
