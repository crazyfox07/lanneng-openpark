package com.lann.openpark.client.dao.mapper;

import com.lann.openpark.client.bean.RegionGateBean;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface GateInfoMapper {
    // 查询所有设备信息
    List<RegionGateBean> findGateInfoAll();
}
