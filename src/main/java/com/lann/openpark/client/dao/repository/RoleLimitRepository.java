package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.RoleLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface RoleLimitRepository extends JpaRepository<RoleLimit, String> {

    /**
     * 根据角色ID删除角色菜单明细信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/6 9:39
     **/
    @Transactional
    @Modifying
    @Query("delete from RoleLimit t where t.roleId = :id")
    void deleteByRoleId(@Param(value = "id") String id);
}
