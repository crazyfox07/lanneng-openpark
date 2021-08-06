package com.lann.openpark.camera.dao.mapper;

import com.github.pagehelper.Page;
import com.lann.openpark.camera.bean.ParkVipBean;
import com.lann.openpark.client.dao.entiy.ParkVipInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface ParkVipInfoMapper {
    /**
     * 根据车牌号码和车牌类型查询VIP信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/8/3 16:23
     **/
    List<ParkVipBean> findParkVipInfo(@Param("plateNo") String plateNo, @Param("plateType") String plateType, @Param("nowDate") String nowDate);

    List<ParkVipBean> findParkVipInfo2(@Param("plateNo") String plateNo, @Param("plateType") String plateType, @Param("nowDate") String nowDate);

    Page<ParkVipInfo> findParkVipInfo4Client(@Param("plateNo") String plateNo, @Param("phoneNo") String pnoneNo);

    Page<ParkVipInfo> queryParkLeaguerList(@Param("plateNo") String plateNo, @Param("phoneNo") String pnoneNo);

}
