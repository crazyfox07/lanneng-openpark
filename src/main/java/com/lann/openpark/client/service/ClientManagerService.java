package com.lann.openpark.client.service;

import com.lann.openpark.client.dao.entiy.ParkLeaguerCars;
import com.lann.openpark.client.dao.repository.ParkLeaguerCarsRepository;
import com.lann.openpark.client.dao.repository.ParkLeaguerRepository;
import com.lann.openpark.system.dao.repository.SysConfigRepository;
import com.lann.openpark.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class ClientManagerService {

    @Autowired
    ParkLeaguerRepository parkLeaguerRepository;
    @Autowired
    ParkLeaguerCarsRepository parkLeaguerCarsRepository;
    @Autowired
    SysConfigRepository sysConfigRepository;

    /**
     * 客户下车辆添加
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 16:00
     **/
    public String leaguerCarAdd(String leaguerId, String plateNo, String plateType) {
        JSONObject jb = new JSONObject();
        try {
            ParkLeaguerCars parkLeaguerCars = new ParkLeaguerCars();
            parkLeaguerCars.setLeaguerId(leaguerId);
            parkLeaguerCars.setPlateNo(plateNo);
            parkLeaguerCars.setPlateType(plateType);
            parkLeaguerCarsRepository.save(parkLeaguerCars);

            jb.put("code", "0");
            jb.put("message", "添加成功");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 客户车辆添加接口异常 ==");
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * 客户下车辆删除
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 16:20
     **/
    public String leaguerCarDelete(String nid) {
        JSONObject jb = new JSONObject();
        try {
            parkLeaguerCarsRepository.deleteById(nid);
            jb.put("code", "0");
            jb.put("message", "删除成功");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 客户车辆删除接口异常 ==");
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * 删除客户信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 16:34
     **/
    public String deleteLeaguer4Client(String leaguerId) {
        JSONObject jb = new JSONObject();
        try {
            // 删除客户车辆信息
            parkLeaguerCarsRepository.deleteAllByLeaguerId(leaguerId);
            // 删除客户信息
            parkLeaguerRepository.deleteById(leaguerId);
            jb.put("code", "0");
            jb.put("message", "删除成功");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 客户车辆删除接口异常 ==");
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * 更新parkdb.sys_config
     *
     * @param config_key
     * @param config_value
     * @return
     */
    public String updateSysConfig(String config_key, String config_value) {
        JSONObject jb = new JSONObject();
        try {
            int result = sysConfigRepository.updateConfigValue(config_key, config_value);
            jb.put("code", "0");
            jb.put("message", result);
            return jb.toString();

        } catch (Exception e) {
            log.error("更新parkdb.sys_config中的字段white_influnce_parking_spot失败");
            log.error(e.toString());
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }
}
