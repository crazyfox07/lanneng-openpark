package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ParkLeaguerCars;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ParkLeaguerCarsRepository extends JpaRepository<ParkLeaguerCars, String> {
    
    public List<ParkLeaguerCars> findByLeaguerId(@Param(value = "leaguerId") String leaguerId);

    public void deleteById(@Param(value = "nid") String nid);

    public void deleteAllByLeaguerId(@Param(value = "leaguerId") String leaguerId);
}
