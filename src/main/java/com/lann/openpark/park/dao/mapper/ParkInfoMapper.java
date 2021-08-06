package com.lann.openpark.park.dao.mapper;

import com.lann.openpark.park.bean.ParkInfoBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface ParkInfoMapper {
   // 根据车牌号查询会员信息
   List<ParkInfoBean> findParkInfo(@Param("parkcode") String parkcode);
   // 更新车位信息
   int updateBerthCount(ParkInfoBean parkInfoBean);
}
