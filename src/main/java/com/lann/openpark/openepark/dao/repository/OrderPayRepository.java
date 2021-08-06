package com.lann.openpark.openepark.dao.repository;

import com.lann.openpark.openepark.dao.entiy.OrderPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 支付记录
 *
 * @Author songqiang
 * @Description
 * @Date 2020/7/10 16:00
 **/
public interface OrderPayRepository extends JpaRepository<OrderPay, String> {
    // 根据订单编号查询订单支付信息
    @Query(value = "select t from OrderPay t where t.orderNo = :orderNo order by t.payTime desc")
    List<OrderPay> findOrderPayByOrderNo(@Param(value = "orderNo") String orderNo);
}
