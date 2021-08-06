package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.ClientRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ClientRoleRepository extends JpaRepository<ClientRole, String> {

    @Query(value = " select t from ClientRole t ")
    List<ClientRole> queryRoleList();

    @Query(value = " select t from ClientRole t where t.id = :id ")
    ClientRole findInfoByRoleid(@Param(value = "id") String id);
}
