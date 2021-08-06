package com.lann.openpark.park.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator.
 */
@Mapper
public interface SysConfigMapper {
    int updateSysConfigByKey(@Param("configKey") String configKey, @Param("configValue") String configValue);
}
