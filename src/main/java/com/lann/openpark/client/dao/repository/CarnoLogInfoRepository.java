package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.CarnoLogInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarnoLogInfoRepository extends JpaRepository<CarnoLogInfo, String> {

//    @Query(value = " select t from BlackInfo t where t.blackId = :blackId ")
//    BlackInfo findBlackInfoByVipId(@Param(value = "blackId") String blackId);


}
