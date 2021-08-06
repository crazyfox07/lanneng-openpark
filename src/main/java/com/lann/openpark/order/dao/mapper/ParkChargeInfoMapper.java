package com.lann.openpark.order.dao.mapper;

import com.github.pagehelper.Page;
import com.lann.openpark.client.bean.*;
import com.lann.openpark.foreign.bean.OrderBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ParkChargeInfoMapper {
    // 更新需要上传的驶入记录随机数
    int updateDriveInRandom(@Param("random") String random);

    // 更新需要上传的驶离记录随机数
    int updateDriveOutRandom(@Param("random") String random);

    // 更新支付记录上传随机数
    int updatePayDetailRandom(@Param("random") String random);

    // 查询订单信息
    Page<OrderInfo> queryOrders(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                @Param("plateNo") String plateNo, @Param("orderState") String orderState,
                                @Param("regionCode") String regionCode, @Param("startDate1") String startDate1, @Param("endDate1") String endDate1);

    // 查询出入记录
    Page<DetectInfo> queryDetectList(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                     @Param("plateNo") String plateNo, @Param("direction") String direction);

    // 查询支付记录
    Page<OrderPayInfo> queryPayList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("plateNo") String plateNo, @Param("payType") String payType, @Param("userid") String userid);

    // 场内车查询
    Page<ParkInnerCars> queryInnerCarList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("plateNo") String plateNo);

    // 场内车删除查询
    Page<DelCars> queryDelOrderList(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                    @Param("plateNo") String plateNo, @Param("userid") String userid, @Param("regionCode") String regionCode);

    // 抬杆记录查询
    Page<PoleControl> queryPoleList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userid") String userid);

    // 手动匹配记录查询
    Page<CarnoLogBean> queryCarnoList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("userid") String userid);

    // 免费放行记录查询
    Page<FreeGos> queryFreeGoList(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("plateNo") String plateNo, @Param("userid") String userid);

    // 查询停车场收入情况
    List<ParkIncomeBean> incomeStatistics(@Param("startDate") String start, @Param("endDate") String end);

    // 查询订单支付信息
    List<OrderPayInfo> queryOrderPay(@Param("nid") String nid);

    // 查询订单信息
    Page<OrderBean> queryOrders4Foreign(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                        @Param("plateNo") String plateNo, @Param("orderState") String orderState);

}
