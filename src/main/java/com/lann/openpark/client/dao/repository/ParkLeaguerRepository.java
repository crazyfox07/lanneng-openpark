package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ParkLeaguer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface ParkLeaguerRepository extends JpaRepository<ParkLeaguer, String> {
    public void deleteById(@Param(value = "leaguerId") String leaguerId);
}
