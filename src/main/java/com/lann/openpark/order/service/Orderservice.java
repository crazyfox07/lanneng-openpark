package com.lann.openpark.order.service;

import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.bean.ParkVipBean;
import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.entiy.ParkDetectInfo;
import com.lann.openpark.camera.service.CameraService;
import com.lann.openpark.common.Constant;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.mapper.ParkChargeInfoMapper;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class Orderservice {
    private static final Logger log = LoggerFactory.getLogger(CameraService.class);

    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;
    @Autowired
    ParkChargeInfoMapper parkChargeInfoMapper;

    public static void main(String[] args) {
        log.info(DateUtil.formatDateYMDHMS(DateUtil.getStartTimeOfDay(new Date())));
    }

    /**
     * 生成订单
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 10:33
     **/
    public Map createOrder(String eparkType, int colorType, String plateNo, String imgPath, EquipGateInfo equipGateInfo, ParkDetectInfo parkDetectInfo, ParkVipBean parkVipBean, CameraData cameraData) {
        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");

        Map<String, Object> map = new HashMap<>();
        Date timeIn = cameraData.getInsertTime();
        boolean mergeOrder = false;// 是否合并订单
        ParkChargeInfo parkChargeInfoMerge = null;
        // 根据车牌号码查询是否存在正在进行中的订单
        String plateType = parkDetectInfo.getLicensetype();
        List<ParkChargeInfo> list = parkChargeInfoRepository.findOutCarOrder(plateNo, plateType);
        if (list.size() > 0) {
            for (ParkChargeInfo parkChargeInfo : list) {
                Date timeInTmp = parkChargeInfo.getCollectiondate1();
                // 循环订单
                if (timeIn.getTime() - timeInTmp.getTime() <= 2 * 60 * 1000) {// 是否有两分钟之内的订单
                    mergeOrder = true;
                    parkChargeInfoMerge = parkChargeInfo;
                }
            }
        }

        if (mergeOrder) {// 订单合并，直接返回之前的订单信息
            log.info("车牌号：" + plateNo + "订单合并");
            map.put("flag", mergeOrder);
            map.put("order", parkChargeInfoMerge);
            return map;
        } else {// 没有需要合并的订单，生成一条订单信息
            // 生成订单数据
            ParkChargeInfo parkChargeInfo = new ParkChargeInfo();
            parkChargeInfo.setEnterNid(parkDetectInfo.getNid());// 驶入记录号
            if (null != parkVipBean) {
                parkChargeInfo.setParkingType(parkVipBean.getVipType());// 白名单
                parkChargeInfo.setVipId(parkVipBean.getVipId());
            } else {
                parkChargeInfo.setParkingType(Constant.PARK_TYPE_TMP);// 临时车
            }
            parkChargeInfo.setVehicleType(eparkType);// epark车辆类型
            parkChargeInfo.setRecordTime(new Date());// 订单生成时间
            parkChargeInfo.setLicensetype(String.valueOf(colorType));// 车牌颜色，（重要，上传epark使用） add by sq 20200706
            parkChargeInfo.setCarno(plateNo);// 车牌号
            parkChargeInfo.setCarnoOriginal(plateNo);
            parkChargeInfo.setPointcodeEnt(equipGateInfo.getPointcode());// 入口编号
            parkChargeInfo.setPointnameEnt(equipGateInfo.getPointname());// 入口名称
            parkChargeInfo.setDevicecode(equipGateInfo.getDevicecode());// 设备编号
            parkChargeInfo.setCpicEnterPath(Constant.IMG_SERVER + imgPath);// 入口图片
            parkChargeInfo.setCarcolor(Constant.CAR_COLOR);//
            parkChargeInfo.setPlatePosition(cameraData.getLocation());// 车牌坐标
            parkChargeInfo.setCollectiondate1(timeIn);// 驶入采集时间
            parkChargeInfo.setPlateType(plateType);// 车牌类型
            parkChargeInfo.setExitType("0");// 0标识未离场
            parkChargeInfo.setInUpload(0);// 入场可以上传
            parkChargeInfo.setOrderType(0);
            // 读取首次免费配置
            String firstFree = properties.getProperty("first_free");
            if ("1".equals(firstFree)) {
                // 查询当日该车在停车场完成的订单的次数
                Date today = DateUtil.getStartTimeOfDay(new Date());
                List<ParkChargeInfo> list_order_day = parkChargeInfoRepository.findCarOrderDay(plateNo, plateType, today);

                parkChargeInfo.setInCounts(list_order_day.size());
            } else {
                parkChargeInfo.setInCounts(-1);
            }
            // 保存订单信息
            parkChargeInfoRepository.saveAndFlush(parkChargeInfo);

            map.put("flag", mergeOrder);
            map.put("order", parkChargeInfo);
            return map;
        }
    }

    
    /**
     * 根据车牌号码和车牌类型查询车辆的订单信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 10:44
     **/
    public List<ParkChargeInfo> findOutCarOrder(String plateNo, String plateType) {
        return parkChargeInfoRepository.findOutCarOrder(plateNo, plateType);
    }

}
