package com.lann.openpark.client.dao.mapper;

import com.lann.openpark.client.dao.entiy.ClientMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator.
 */
@Mapper
public interface ParkUserInfoMapper {

    List<ClientMenu> findMenuList(@Param("userid") String userid, @Param("parent") String parent);
}
