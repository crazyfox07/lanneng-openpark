package com.lann.openpark.camera.dao.mapper;

import com.github.pagehelper.Page;
import com.lann.openpark.camera.dao.entiy.AbnormalOut;
import com.lann.openpark.camera.dao.entiy.AbnormalOutDetail;
import com.lann.openpark.client.dao.entiy.BlackInfo;
import com.lann.openpark.client.dao.entiy.ParkVipInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface BlackInfoMapper {

    List<BlackInfo> findBlackInfo(@Param("plateNo") String plateNo, @Param("plateType") String plateType, @Param("nowDate") String nowDate);

    Page<ParkVipInfo> findParkBlackInfo4Client(@Param("plateNo") String plateNo, @Param("phoneNo") String pnoneNo);

    Page<AbnormalOut> queryAbnormalkList(@Param("plateNo") String plateNo, @Param("num") String num);

    Page<AbnormalOutDetail> queryAbnormalDetailList(@Param("aid") String aid);
}
