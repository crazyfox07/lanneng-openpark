package com.lann.openpark.camera.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EquipSendMsgMapper {
    int updateEquipSendMsgVoiceRandom(@Param("random") String random, @Param("serialno") String serialno);
}
