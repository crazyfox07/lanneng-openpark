package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ClientMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ClientMenuRepository extends JpaRepository<ClientMenu, String> {
    @Query(value = " select t from ClientMenu t where t.mParent = :parentId order by t.mOrder ")
    List<ClientMenu> findFirstLevelMenu(@Param(value = "parentId") String parentId);
}
