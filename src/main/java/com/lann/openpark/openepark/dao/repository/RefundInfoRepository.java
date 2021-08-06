package com.lann.openpark.openepark.dao.repository;

import com.lann.openpark.openepark.dao.entiy.RefundInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 退款记录
 * @Author songqiang
 * @Description 
 * @Date 2020/7/10 16:00
**/
public interface RefundInfoRepository extends JpaRepository<RefundInfo,String> {

}
