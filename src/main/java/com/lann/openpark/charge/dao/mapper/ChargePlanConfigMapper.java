package com.lann.openpark.charge.dao.mapper;

import com.lann.openpark.charge.dao.entiy.ChargePlanConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChargePlanConfigMapper {
    /**
     * 根据车牌类型查询收费方案，收费策略
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 15:45
     **/
    List<ChargePlanConfig> findChargePolicyByPlateType(@Param("plateType") String plateType, @Param("regionCode") String regionCode);
}
