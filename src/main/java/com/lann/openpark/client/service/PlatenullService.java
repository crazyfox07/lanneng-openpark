package com.lann.openpark.client.service;

import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.entiy.GateMsg;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.repository.CameraDataRepository;
import com.lann.openpark.camera.dao.repository.ManualTriggerRepository;
import com.lann.openpark.camera.service.CameraService;
import com.lann.openpark.common.Constant;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class PlatenullService {
    private static final Logger log = LoggerFactory.getLogger(PlatenullService.class);

    @Autowired
    CameraDataRepository cameraDataRepository;
    @Autowired
    EquipInfoMapper equipInfoMapper;
    @Autowired
    CameraService cameraService;
    @Autowired
    ManualTriggerRepository manualTriggerRepository;

    /**
     * 主函数测试
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/12 9:33
     **/
    public static void main(String[] args) {
        // log.info("临时车牌生成：" + getTempPlateNo());
    }


    /**
     * 生成临时车牌号码的方法
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/12 9:42
     **/
    private String getTempPlateNo() {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            if ("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        // log.info("Number: " + val.toUpperCase());
        val = "好L" + val;
        return val.toUpperCase();
    }

    /**
     * 无牌车驶入方法
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/12 9:48
     **/
    public JSONObject noplatein(String parkCode, String deviceCode, String tempPlateNo) {
        JSONObject jb = new JSONObject();
        // 缓存
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // 车辆类型设置为1
            int plateType = 1;// 车牌类型，按小型车处理
            // 手动触发识别（写缓存）
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(deviceCode);
            ehcacheUtil.put(Constant.PARK_CACHE, "manual_trigger_" + deviceCode, gateMsg);
            CameraData cameraData = null;
            Thread.sleep(500);
            for (int i = 0; i < 5; i++) {
                log.info("第 " + (i + 1) + " 次读取缓存...");
                // 从缓存中获取cameraData的ID号
                cameraData = (CameraData) ehcacheUtil.get(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                if (null != cameraData) {
                    // 删除缓存
                    log.info("读取到识别ID: " + cameraData.getId());
                    ehcacheUtil.remove(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                    break;
                } else {
                    Thread.sleep(1000);
                }
            }
            // 是否在时间内查询到cameraData
            if (null == cameraData) {
                jb.put("code", "1");
                jb.put("message", "trigger data error!");
                return jb;
            }
            // 查询上传识别结果
//            CameraData cameraData = cameraDataRepository.findCameraDataByNid(camId);
//            if (null == cameraData) {
//                jb.put("code", "2");
//                jb.put("message", "query CamreaData error!");
//                return jb;
//            }
            // 组装cameradata
            cameraData.setLicense(tempPlateNo);
            cameraData.setPlateType(plateType);
            cameraDataRepository.save(cameraData);
            // 查询设备信息
            List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(deviceCode);
            if (list_equip.size() <= 0) {
                jb.put("code", "3");
                jb.put("message", "query equipInfo error!");
                return jb;
            }
            EquipGateInfo equipGateInfo = list_equip.get(0);
            // 调用驶入!!!!!!!
            cameraService.parkDriveIn(cameraData, equipGateInfo);
            // 数据返回
            jb.put("code", "0");
            jb.put("message", "success");
            jb.put("tempPlateNo", tempPlateNo);
            jb.put("plateType", plateType);
            return jb;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 无牌车驶入方法接口异常 ==");
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            return jb;
        }
    }

    /**
     * 无牌车驶离
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/21 10:40
     **/
    public JSONObject noplateout(String deviceCode, String tempPlateNo) {
        JSONObject jb = new JSONObject();
        // 缓存
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // 车辆类型设置为1
            int plateType = 1;// 车牌类型，按小型车处理
            // 手动触发识别（写缓存）
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(deviceCode);
            ehcacheUtil.put(Constant.PARK_CACHE, "manual_trigger_" + deviceCode, gateMsg);
            CameraData cameraData = null;
            Thread.sleep(2000);
            for (int i = 0; i < 8; i++) {
                log.info("第 " + (i + 1) + " 次读取缓存...");
                // 从缓存中获取cameraData的ID号
                cameraData = (CameraData) ehcacheUtil.get(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                if (null != cameraData) {
                    // 删除缓存
                    log.info("读取到识别ID: " + cameraData.getId());
                    ehcacheUtil.remove(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                    break;
                } else {
                    Thread.sleep(1000);
                }
            }
            // 是否在时间内查询到cameraData
            if (null == cameraData) {
                jb.put("code", "1");
                jb.put("message", "drive out trigger data error!");
                return jb;
            }
//            // 查询上传识别结果
//            CameraData cameraData = cameraDataRepository.findCameraDataByNid(camId);
//            if (null == cameraData) {
//                jb.put("code", "2");
//                jb.put("message", "drive out query CamreaData error!");
//                return jb;
//            }
            // 组装cameradata
            cameraData.setLicense(tempPlateNo);
            cameraData.setPlateType(plateType);
            cameraDataRepository.save(cameraData);
            // 查询设备信息
            List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(deviceCode);
            if (list_equip.size() <= 0) {
                jb.put("code", "3");
                jb.put("message", "drive out query equipInfo error!");
                return jb;
            }
            EquipGateInfo equipGateInfo = list_equip.get(0);
            // 调用驶离!!!!!!!
            cameraService.parkDriveOut(cameraData, equipGateInfo);
            // 数据返回
            jb.put("code", "0");
            jb.put("message", "success");
            jb.put("tempPlateNo", tempPlateNo);
            jb.put("plateType", plateType);
            return jb;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 无牌车驶离方法接口异常 ==");
            jb.put("code", "99");
            jb.put("message", "Exception：" + e.getMessage());
            return jb;
        }
    }

}
