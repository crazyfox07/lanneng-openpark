package com.lann.openpark.park.service;

import com.lann.openpark.camera.bean.ParkVipBean;
import com.lann.openpark.camera.dao.mapper.ParkVipInfoMapper;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.dao.entiy.ChargePlanConfig;
import com.lann.openpark.charge.dao.mapper.ChargePlanConfigMapper;
import com.lann.openpark.charge.service.ChargeService;
import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import com.lann.openpark.client.dao.repository.ParkRegionInfoRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.park.bean.ParkInfoBean;
import com.lann.openpark.park.dao.mapper.ParkInfoMapper;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class ParkService {

    private static final Logger log = LoggerFactory.getLogger(ParkService.class);

    // 更新泊位锁
    private static final int countLock = 0;

    @Autowired
    ParkInfoMapper parkInfoMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ParkRegionInfoRepository parkRegionInfoRepository;
    @Autowired
    ChargePlanConfigMapper chargePlanConfigMapper;
    @Autowired
    ChargeService chargeService;
    @Autowired
    ParkVipInfoMapper parkVipInfoMapper;

    /**
     * 查询停车场信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/6 14:57
     **/
    public ParkInfoBean findParkInfo() throws Exception {
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String parkCode = properties.getProperty("park_code");

        // 查询车位信息
        List<ParkInfoBean> list_park = parkInfoMapper.findParkInfo(parkCode);
        if (list_park.size() <= 0) {
            throw new Exception("未查询到停车场信息");
        }
        return list_park.get(0);
    }


    /**
     * 查询停车场区域信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 11:51
     **/
    public ParkRegionInfo findParkRegionInfo(String regionCode) throws Exception {
        ParkRegionInfo region = parkRegionInfoRepository.findRegionInfoByRegionCode(regionCode);
        return region;
    }

    public ParkingInfo getParkingFee(Date timeIn, Date timeOut, int plateType, boolean isFreeTime, String regionCode) throws Exception {
        double fee = 0;// 停车费用
        // 驶入驶离时间转换
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeInStr = DateUtil.formatDateYMDHMS(timeIn);
        String timeOutStr = DateUtil.formatDateYMDHMS(timeOut);
        DateTime timeInJoda = new DateTime(df.parse(timeInStr));
        DateTime timeOutJoda = new DateTime(df.parse(timeOutStr));
        // 生成停车信息
        ParkingInfo parkingInfo = new ParkingInfo(timeInJoda.toDate(), timeOutJoda.toDate());
        // 查询停车场计费方案配置
        List<ChargePlanConfig> list_charge_plan_config = chargePlanConfigMapper.findChargePolicyByPlateType(String.valueOf(plateType), regionCode);
        if (list_charge_plan_config.size() <= 0) {// 没有有效的收费配置
            log.error("【查询计费方案配置】--plateType：" + plateType + "的车牌类型没有配置收费方案");
            parkingInfo.setCost(0);
            return parkingInfo;
        }
        // 计费规则ID
        String policyId = list_charge_plan_config.get(0).getPolicyId();
        if (StringUtils.isEmpty(policyId)) {// 没有有效的收费配置
            log.error("【查询计费方案配置】--policyId为空");
            parkingInfo.setCost(0);
            return parkingInfo;
        }
        // 计算费用
        parkingInfo = chargeService.getParkingFee(parkingInfo, policyId, isFreeTime);

        return parkingInfo;

    }

    /**
     * 停车场区域车位更新方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/6 15:21
     **/
    public int updateBerthCount(int cars, String regionCode) throws Exception {

        // 设置一个同步锁，将更新车位做成线程安全
        synchronized ((Object) countLock) {
            // ParkInfoBean parkInfoBean = this.findParkInfo();
            ParkRegionInfo region = this.findParkRegionInfo(regionCode);
            int rectifyCount = region.getJudgeRemainParkingNumber() == null ? 0 : region.getJudgeRemainParkingNumber().intValue();
            int remainBerthCount = region.getRegionRectifyCount() + cars + rectifyCount;// 车位剩余数 = 车位剩余数（+-）1后再计算调整数
            // parkInfoBean.setRemainBerthCount(remainBerthCount);// 重新设置车位剩余数
            region.setRegionRectifyCount(remainBerthCount);// 重新设置车位剩余数
            if (rectifyCount != 0) {
                region.setJudgeRemainParkingNumber(0);// 车位调整数重新设置为0
            }
            parkRegionInfoRepository.save(region);
            return 1;
        }

    }

    /**
     * @param cars
     * @param regionCode
     * @param plateNo
     * @param plateType
     * @return
     * @throws Exception
     */
    public int updateBerthCount(int cars, String regionCode, String plateNo, String plateType) throws Exception {

        // 设置一个同步锁，将更新车位做成线程安全
        synchronized ((Object) countLock) {
            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            // 0白名单不影响车位, 1白名单影响车位
            if (properties.getProperty("white_influnce_parking_spot").equals("0")) {
                List<ParkVipBean> list_vip = parkVipInfoMapper.findParkVipInfo(plateNo, plateType, DateUtil.toDate("yyyy-MM-dd"));
                if (list_vip.size() > 0) {
                    log.info("会员车辆，不计入车位数计算");
                    return 1;
                }
            }
            // ParkInfoBean parkInfoBean = this.findParkInfo();
            ParkRegionInfo region = this.findParkRegionInfo(regionCode);
            int rectifyCount = region.getJudgeRemainParkingNumber() == null ? 0 : region.getJudgeRemainParkingNumber().intValue();
            int remainBerthCount = region.getRegionRectifyCount() + cars + rectifyCount;// 车位剩余数 = 车位剩余数（+-）1后再计算调整数
            // parkInfoBean.setRemainBerthCount(remainBerthCount);// 重新设置车位剩余数
            region.setRegionRectifyCount(remainBerthCount);// 重新设置车位剩余数
            if (rectifyCount != 0) {
                region.setJudgeRemainParkingNumber(0);// 车位调整数重新设置为0
            }
            parkRegionInfoRepository.save(region);
            return 1;
        }

    }


}
