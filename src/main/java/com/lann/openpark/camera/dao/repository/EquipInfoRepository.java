package com.lann.openpark.camera.dao.repository;

import com.lann.openpark.camera.dao.entiy.EquipInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EquipInfoRepository extends JpaRepository<EquipInfo, String> {
    /**
     * 根据设备编号查询设备信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/8 8:57
     **/
    @Cacheable(value = "park_equip", key = "'equip_' + #devicecode")// 添加ehcache缓存
    @Query(value = "select t from EquipInfo t where t.devicecode = :devicecode ")
    EquipInfo findEquipInfoByDevicecode(@Param(value = "devicecode") String devicecode);

    /**
     * 查询所有主设备
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/14 9:44
     **/
    @Cacheable(value = "park_equip", key = "'equip_list'")// 添加ehcache缓存
    @Query(value = "select t from EquipInfo t where t.firstSecond = 1 ")
    List<EquipInfo> findEquipList();

    /**
     * 查询所有设备信息（没有缓存）
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 14:54
     **/
    @Query(value = "select t from EquipInfo t where t.firstSecond = 1 ")
    List<EquipInfo> findEquipListNocache();
}
