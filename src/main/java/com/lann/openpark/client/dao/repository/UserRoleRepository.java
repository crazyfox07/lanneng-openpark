package com.lann.openpark.client.dao.repository;

import com.lann.openpark.client.dao.entiy.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    /**
     * 保存用户角色信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/6 9:50
     **/
    @Transactional
    @Modifying
    @Query("delete from UserRole t where t.userId = :userid")
    void deleteRoleByUserId(@Param(value = "userid") String userid);

    /**
     * 根据userId查询授权信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/6 15:12
     **/
    @Query(value = " select t from UserRole t where t.roleId = :roleId ")
    List<UserRole> findUserRoleByRoleId(@Param(value = "roleId") String roleId);
}
