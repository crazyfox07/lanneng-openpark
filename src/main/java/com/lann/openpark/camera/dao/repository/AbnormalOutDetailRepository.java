package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.AbnormalOutDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface AbnormalOutDetailRepository extends JpaRepository<AbnormalOutDetail, String> {

    @Transactional
    @Modifying
    @Query("delete from AbnormalOutDetail t where t.aid = :id ")
    int deleByAid(@Param(value = "id") String id);

    @Query(value = " select t from AbnormalOutDetail t where t.aid = :aid ")
    List<AbnormalOutDetail> findDetailByAid(@Param(value = "aid") String aid);
}
