package com.lann.openpark.camera.service;

import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.bean.EtcBean;
import com.lann.openpark.camera.bean.ParkVipBean;
import com.lann.openpark.camera.dao.entiy.*;
import com.lann.openpark.camera.dao.mapper.BlackInfoMapper;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.mapper.EquipSendMsgMapper;
import com.lann.openpark.camera.dao.mapper.ParkVipInfoMapper;
import com.lann.openpark.camera.dao.repository.*;
import com.lann.openpark.camera.threads.*;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.service.ChargeService;
import com.lann.openpark.charge.util.FileOperateUtil;
import com.lann.openpark.client.dao.entiy.BlackInfo;
import com.lann.openpark.client.dao.entiy.ParkLeaguerCars;
import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import com.lann.openpark.client.dao.repository.ParkLeaguerCarsRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.common.ParkCommon;
import com.lann.openpark.common.response.ResponseResult;
import com.lann.openpark.etc.service.EtcService;
import com.lann.openpark.openepark.dao.entiy.OrderPay;
import com.lann.openpark.openepark.dao.repository.OrderPayRepository;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.order.service.Orderservice;
import com.lann.openpark.park.service.ParkService;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.VoiceLedUtil;
import com.lann.openpark.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Transactional
@Service
@Slf4j
public class CameraService {

    @Autowired
    CameraDataRepository cameraDataRepository;
    @Autowired
    EquipInfoRepository equipInfoRepository;
    @Autowired
    ParkDetectInfoRepository parkDetectInfoRepository;
    @Autowired
    ParkInnerCarsRepository parkInnerCarsRepository;
    @Autowired
    EquipCometMsgRepository equipCometMsgRepository;
    @Autowired
    EquipSendMsgRepository equipSendMsgRepository;
    @Autowired
    ManualTriggerRepository manualTriggerRepository;
    @Autowired
    LedConfigRepository ledConfigRepository;
    @Autowired
    VoiceConfigRepository voiceConfigRepository;
    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;

    @Autowired
    EquipInfoMapper equipInfoMapper;
    @Autowired
    ParkVipInfoMapper parkVipInfoMapper;
    @Autowired
    BlackInfoMapper blackInfoMapper;
    @Autowired
    EquipSendMsgMapper equipSendMsgMapper;

    @Autowired
    ParkService parkService;
    @Autowired
    Orderservice orderservice;
    @Autowired
    ChargeService chargeService;
    @Autowired
    OpenEparkService openEparkService;
    @Autowired
    EtcService etcService;
    @Autowired
    OrderPayRepository orderPayRepository;
    @Autowired
    SysCarConfigRepository sysCarConfigRepository;
    @Autowired
    AbnormalOutRepository abnormalOutRepository;
    @Autowired
    AbnormalOutDetailRepository abnormalOutDetailRepository;
    @Autowired
    ParkLeaguerCarsRepository parkLeaguerCarsRepository;

    @Value("${parkInfo.isEpark}")
    private boolean isEpark;// 是否对接好停车平台
    @Value("${parkInfo.isVoice}")
    private boolean isVoice;// 是否下发语音
    // 手动触发类型
    private final static int MANAUL_TRIGGERTYPE = 4;
    // 识别车牌号为“无”
    private final static String NULL_PLATE = "-无-";

    public static void main(String[] args) throws Exception {
//        String data = "您好";
//        byte[] bt2 = data.getBytes("GB2312");
//        byte[] bt1 = new byte[]{0x00, 0x64, (byte) 0xFF, (byte) 0xFF, 0x30, (byte) (bt2.length + 1), (byte) 1};
//        byte[] bt3 = new byte[bt1.length + bt2.length];
//        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
//        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
//        byte[] bt4 = CRC16Util.getCrc16(bt3);
//        byte[] bt5 = new byte[bt3.length + bt4.length];
//        System.arraycopy(bt3, 0, bt5, 0, bt3.length);
//        System.arraycopy(bt4, 0, bt5, bt3.length, bt4.length);
//        System.out.println(bt5.length);
//        System.out.println(HexUtil.bytesToHexString(bt5).toUpperCase());
//        System.out.println(Base64Util.byteArrToBase64(bt5));


//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date d1 = df.parse("2020-09-06 23:59:59");
//        DateTime t1 = new DateTime(d1);
//        DateTime t2 = new DateTime(new Date());
//        int remain_Days = Days.daysBetween(t2.withTimeAtStartOfDay(), t1.withTimeAtStartOfDay()).getDays();
//        log.info("剩余天数：" + remain_Days);

//        DateTime now = new DateTime(new Date());
//        log.info(now.getYear() + "");
//        log.info(now.getMonthOfYear() + "");
//        log.info(now.getDayOfMonth() + "");
//        log.info(now.getDayOfWeek() + "");
//        log.info(now.getHourOfDay() + "");
//        log.info(now.getMinuteOfHour() + "");
//        log.info(now.getSecondOfMinute() + "");

//        String path = "C:\\apache-tomcat-7.0.96\\webapps\\FileUpload";
//        if (path.endsWith("\\")) {
//            log.info("111111");
//        } else {
//            log.info("222222");
//        }
        String path = "/FileUpload/parkImgs\\20210728\\202107281105550rgjqqw.jpg";
        System.out.println(path.replaceAll("\\\\", "/"));
    }

    /**
     * 相机上报接口
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/2 8:48
     **/
    public String camData(String jsonStr) throws Exception {
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        CameraData cameraData = analysisCameraDate(jsonStr);
        //////////////以下是业务操作流程//////////////////////////////
        String deviceCode = cameraData.getSerialNo();// 相机序列号
        // 根据相机序列号查询停车场设备信息
        List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(deviceCode);
        if (list_equip.size() <= 0) {
            log.error("【相机数据上报】--停车场配置错误，无出入口配置信息或配置错误");
            return ResponseResult.FAIL().toString();
        }
        EquipGateInfo equipGateInfo = list_equip.get(0);
        String videofunc = equipGateInfo.getVideofunc();// 判断设备出入口设备
        if (Constant.DRIVE_IN_CODE.equals(videofunc)) {// 驶入
            if (MANAUL_TRIGGERTYPE == cameraData.getTriggerType()) {// 无牌车识别，扫码触发
                ehcacheUtil.put(Constant.PARK_CACHE, "trigger_result_" + equipGateInfo.getDevicecode(), cameraData);
                return ResponseResult.SUCCESS().toString();
            } else {
                String inRet = parkDriveInPre(cameraData, equipGateInfo);
                return inRet;
            }
        } else if (Constant.DRIVE_OUT_CODE.equals(videofunc)) {// 驶离
            if (MANAUL_TRIGGERTYPE == cameraData.getTriggerType()) {// 手动匹配触发上传
                ehcacheUtil.put(Constant.PARK_CACHE, "trigger_result_" + equipGateInfo.getDevicecode(), cameraData);
                return ResponseResult.SUCCESS().toString();
            } else {
                String outRet = parkDriveOutPre(cameraData, equipGateInfo);
                return outRet;
            }
        } else {
            log.error("【相机数据上报】--停车场出入口信息配置错误，无出入方向");
            return ResponseResult.FAIL().toString();
        }
        //////////////以下是业务操作流程//////////////////////////////
    }

    /**
     * 插入手动触发识别结果
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 16:22
     **/
    private void insertManualTriggerMsg(CameraData cameraData) {
        String plateNo = cameraData.getLicense();
        int plateType = cameraData.getPlateType();
        String orderNo = "";
        if (!NULL_PLATE.equals(cameraData.getLicense())) {
            log.info("【手动识别】车的车牌号码：" + plateNo);
            List<ParkChargeInfo> list_order = orderservice.findOutCarOrder(plateNo, String.valueOf(plateType));
            if (list_order.size() > 0) {
                ParkChargeInfo parkChargeInfo = list_order.get(0);
                orderNo = parkChargeInfo.getNid();
            } else {
                log.info("根据【手动识别】车牌查询订单为空！");
            }
        } else {
            log.info("【手动识别】未识别到车牌号码");
        }
        ManualTrigger manualTrigger = new ManualTrigger();
        manualTrigger.setOrderNo(orderNo);
        manualTrigger.setPlateNo(plateNo);
        manualTrigger.setPlateType(String.valueOf(plateType));
        manualTrigger.setDevicecode(cameraData.getSerialNo());// 设备编号
        log.info("【手动识别】保存手动识别结果！");
        manualTriggerRepository.save(manualTrigger);
    }

    /**
     * 驶入前置
     * 判断区域类型（独立区域还是嵌套区域）
     *
     * @Author songqiang
     * @Description
     * @Date 2021/8/2 11:50
     **/
    public String parkDriveInPre(CameraData cameraData, EquipGateInfo equipGateInfo) throws Exception {
//        后期可以根据需求扩展嵌套单独计费
//        单独计费，又涉及嵌套区域和上级区域是不是一家管理的，不是一家的，需要上传两个订单，上级区域出口即嵌套区域入口
//        如果是一家管理，可以做嵌套订单，嵌套订单，单独计费，然后把嵌套订单的费用加到上级区域订单费用上
//        嵌套区域设计上，还分是不是完全嵌套和嵌套区域在边界，可以从嵌套区域直接离场的情况
//        情况太多了，只能根据序曲再去设计了，很难做一套全面的方案
        if (Constant.REGION_TYPE_1.equals(equipGateInfo.getRegionType())) {
            // 嵌套管控
            // 实现市医院需求，嵌套区域设置限制进出
            // 嵌套管控模式只管控车位数，不生成订单，不播报（后期可加）
            return parkDriveInNested(cameraData, equipGateInfo);
        } else {
            // 独立区域，按正常的流程走
            return parkDriveIn(cameraData, equipGateInfo);
        }
    }

    /**
     * 生成嵌套订单
     *
     * @Author songqiang
     * @Description
     * @Date 2021/8/2 15:01
     **/
    public String parkDriveInNested(CameraData cameraData, EquipGateInfo equipGateInfo) throws Exception {
        String plateNo = cameraData.getLicense();// 车牌
        int plateType = cameraData.getPlateType();// 车牌类型
        if (StringUtils.isEmpty(plateNo) || NULL_PLATE.equals(plateNo)) {// 车牌号为空，不放行
            return ResponseResult.SUCCESS().toString();
        }
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        ParkVipBean parkVipBean = this.getVipInfo(plateNo, String.valueOf(plateType));
        // 1.判断停车场是否限制满场进出
        String carsLimit = equipGateInfo.getRestrictedAccess();// 是否限制进出
        boolean isIn = false;// 车辆进入权限
        if ("1".equals(carsLimit)) {
            // 剩余车位放在【区域信息】上
            ParkRegionInfo region = parkService.findParkRegionInfo(equipGateInfo.getRegionCode());
            int parkNums = region.getRegionRectifyCount();
            if (parkNums <= 0) {// 没有剩余车位
                // 获取白名单特权
                String carsLimitWhite = equipGateInfo.getWhitelistPrivileges();// 车位数剩余为0，白名单车辆进入
                if ("1".equals(carsLimitWhite)) {
                    if (null == parkVipBean) {
                        isIn = true;
                    }
                } else {
                    isIn = true;
                }
                if (isIn) {
                    log.info("车位已满，限制进入：" + DateUtil.formatDateYMDHMS(new Date()));
                    // 下发语音，下发显示屏显示
                    if (isVoice) {
                        int voiceType = 8;
                        int ledType = 8;
                        Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                        thread.start();
                    }
                    return ResponseResult.SUCCESS().toString();
                }
            }
        }

        ////////// 订单操作--其他模式需要考虑 ///////////////////////////////////
        ////////// 订单操作--其他模式需要考虑 ///////////////////////////////////

        // 2.开闸
        GateMsg gateMsg = new GateMsg();
        gateMsg.setDevicecode(equipGateInfo.getDevicecode());
        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
        // 3.车位数减1
        parkService.updateBerthCount(-1, equipGateInfo.getRegionCode(), plateNo, String.valueOf(plateType));
        // 语音消息下发
        if (isVoice) {
            // 会员车和非会员车播报不一样
            int voiceType = 1;
            int remain_Days = 0;// 会员车剩余天数
            if (parkVipBean != null) {
                voiceType = 2;
            }
            // voiceType = 1||2
            // ledType = 5
            Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, 5,
                    parkVipBean, 1000, 0));
            thread.start();
        }
        // 返回开闸信息
        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 停车场驶入
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/2 9:00
     **/
    public String parkDriveIn(CameraData cameraData, EquipGateInfo equipGateInfo) throws Exception {
        String plateNo = cameraData.getLicense();// 车牌
        int plateType = cameraData.getPlateType();// 车牌类型
        if (StringUtils.isEmpty(plateNo) || NULL_PLATE.equals(plateNo)) {// 车牌号为空，不放行
            return ResponseResult.SUCCESS().toString();
        }
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");

        // 1.vip中p
        List<SysCarConfig> list_car_config = sysCarConfigRepository.findSysCarConfig(plateNo, String.valueOf(plateType));
        if (list_car_config.size() > 0) {
            // 放行
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(equipGateInfo.getDevicecode());
            // 写缓存
            ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
            return ResponseResult.SUCCESS().toString();
        }

        // 2.查询黑名单信息
        List<BlackInfo> list_black = blackInfoMapper.findBlackInfo(plateNo, String.valueOf(plateType), DateUtil.toDate("yyyy-MM-dd"));
        if (list_black.size() > 0) {
            // 黑名单车辆
            // 屏显
            int voiceType = 10;
            int ledType = 11;
            Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
            thread.start();
            return ResponseResult.SUCCESS().toString();
        }

        // 3.异常驶离控制
        String abnormalOut = properties.getProperty("abnormal_out");
        if ("1".equals(abnormalOut)) {
            int abnormalNum = Integer.valueOf(properties.getProperty("abnormal_num"));// 阈值
            // 根据车牌号查询异常记录
            AbnormalOut bean = abnormalOutRepository.findAbnormalOutByCar(plateNo, String.valueOf(plateType));
            if (null != bean) {// 有记录
                if (bean.getCumulNum() >= abnormalNum) {// 异常驶离次数大于阈值，禁止车辆进入
                    int voiceType = 10;
                    int ledType = 12;
                    Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                    thread.start();
                    return ResponseResult.SUCCESS().toString();
                }
            }
        }

        // 4.查询车牌VIP信息
        // getVipInfo会根据白名单类型的不同，查询该车是否符合白名单
        ParkVipBean parkVipBean = this.getVipInfo(plateNo, String.valueOf(plateType));
//        List<ParkVipBean> list_vip = parkVipInfoMapper.findParkVipInfo(plateNo, String.valueOf(plateType), DateUtil.toDate("yyyy-MM-dd"));
//        ParkVipBean parkVipBean = null;
//        if (list_vip.size() > 0) {
//            parkVipBean = list_vip.get(0);
//            // log.info("VIP信息：" + parkVipBean);
//        }

        // 5.通行方式
        if ("1".equals(equipGateInfo.getPointFunc())) {// 只白名单放行
            boolean isTscl = true;
            // 特殊车辆放行
            if (5 == cameraData.getPlateType() || 6 == cameraData.getPlateType() || 8 == cameraData.getPlateType() || 9 == cameraData.getPlateType() ||
                    16 == cameraData.getPlateType() || 17 == cameraData.getPlateType()) {
                isTscl = false;
            }
            if (null == parkVipBean && isTscl) {// 不放行，下发语音屏显，不开闸
                int voiceType = 10;
                int ledType = 10;
                Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                thread.start();
                return ResponseResult.SUCCESS().toString();
            }
        }

        // 6.获取白名单配置
        String isOnlyWhiteIn = properties.getProperty("is_only_white_in");// 车场只允许白名单车辆进出
        if ("1".equals(isOnlyWhiteIn)) {
            if (null == parkVipBean) {
                int voiceType = 9;
                int ledType = 9;
                Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                thread.start();
                return ResponseResult.SUCCESS().toString();
            }
        }

        // 7.判断停车场是否限制满场进出
        // String carsLimit = properties.getProperty("cars_limit");// 是否限制进出
        String carsLimit = equipGateInfo.getRestrictedAccess();// 是否限制进出
        boolean isIn = false;// 车辆进入权限
        if ("1".equals(carsLimit)) {
            // 查询停车场剩余车位数，如果剩余车位数
            // ParkInfoBean park = parkService.findParkInfo();
            // 获取车位剩余数，车位剩余数=车位剩余数+调整数
            // int parkNums = park.getRemainBerthCount() + park.getRectifyCount();
            // 剩余车位放在【区域信息】上
            ParkRegionInfo region = parkService.findParkRegionInfo(equipGateInfo.getRegionCode());
            int parkNums = region.getRegionRectifyCount();
            if (parkNums <= 0) {// 没有剩余车位
                // 获取白名单特权
                // String carsLimitWhite = properties.getProperty("cars_limit_white");// 车位数剩余为0，白名单车辆进入
                String carsLimitWhite = equipGateInfo.getWhitelistPrivileges();// 车位数剩余为0，白名单车辆进入
                if ("1".equals(carsLimitWhite)) {
                    if (null == parkVipBean) {
                        isIn = true;
                    }
                } else {
                    isIn = true;
                }
                if (isIn) {
                    log.info("车位已满，限制进入：" + DateUtil.formatDateYMDHMS(new Date()));
                    // 下发语音，下发显示屏显示
                    if (isVoice) {
                        int voiceType = 8;
                        int ledType = 8;
                        Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                        thread.start();
                    }
                    return ResponseResult.SUCCESS().toString();
                }

            }
        }

        String imgPath = "";// 图片路径
        // 8.图片上传
        imgPath = this.uploadImgOne(cameraData);
        int colorType = Constant.convertColorType4OpenEpark(cameraData.getColorType());
        // 9.车牌类型转换（epark使用）
        String eparkType = Constant.convertPlateOpenEpark4Epark(colorType);
        // 10.插入入场信息
        ParkDetectInfo parkDetectInfo = this.saveDetectInfo(plateNo, plateType, eparkType, imgPath, Constant.DRIVE_IN_CODE, equipGateInfo, cameraData, parkVipBean == null ? "" : parkVipBean.getVipId());
        // 11.生成订单信息
        Map map = orderservice.createOrder(eparkType, colorType, plateNo, imgPath, equipGateInfo, parkDetectInfo, parkVipBean, cameraData);
        boolean isOrderCombine = (boolean) map.get("flag");// 是否是合并的订单
        ParkChargeInfo parkChargeInfo = (ParkChargeInfo) map.get("order");// 订单信息
        // 12.桌面推送
        String isDesk = properties.getProperty("is_desk");
        if ("1".equals(isDesk)) {
            WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_IN_MESSAGE, "", "");
        }

        // 13.开闸消息插入缓存
        GateMsg gateMsg = new GateMsg();
        gateMsg.setDevicecode(equipGateInfo.getDevicecode());
        // 14.写缓存
        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
        if (isOrderCombine) {
            // 语音消息下发
            if (isVoice) {
                // 会员车和非会员车播报不一样
                int voiceType = 1;
                // boolean isvip = false;
                int remain_Days = 0;// 会员车剩余天数
                if (parkVipBean != null) {
                    voiceType = 2;
                    // isvip = true;
                }
                // 发送车辆驶入消息
                // voiceType = 1||2
                // ledType = 5
                Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, 5,
                        parkVipBean, 1000, 0));
                thread.start();
            }
        } else {
            // 插入在场车辆信息（park_inner_cars）
            ParkInnerCars parkInnerCars = new ParkInnerCars();
            parkInnerCars.setNid(parkDetectInfo.getNid());
            parkInnerCars.setCarno(plateNo);
            if (null != parkVipBean) {
                parkInnerCars.setParkingType(parkVipBean.getVipType());// 白名单
                parkInnerCars.setGroupNid(parkVipBean.getGroupNid());
                parkInnerCars.setVipOutnumber(parkVipBean.getVipId());
            }
            parkInnerCars.setRegionCode(equipGateInfo.getRegionCode());
            parkInnerCars.setPlateType(String.valueOf(plateType));
            parkInnerCarsRepository.save(parkInnerCars);
            // 更新停车场车位信息
            parkService.updateBerthCount(-1, equipGateInfo.getRegionCode(), plateNo, String.valueOf(plateType));
            // 语音消息下发
            if (isVoice) {
                // 会员车和非会员车播报不一样
                int voiceType = 1;
                boolean isvip = false;
                if (parkVipBean != null) {
                    voiceType = 2;
                    isvip = true;
                }
                // 发送车辆驶入消息
                // voiceType = 1||2
                // ledType = 5
                Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, 5, parkVipBean, 1000, 0));
                thread.start();
            }
            // 驶入数据上传
            if (isEpark) {
                // 执行驶入数据上传
                Thread thread = new Thread((Runnable) new UpLoadDriveInThread(parkChargeInfo, cameraData));
                thread.start();
            }
        }
        // 返回开闸信息
        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 停车场驶离前置
     *
     * @Author songqiang
     * @Description
     * @Date 2021/8/2 17:29
     **/
    public String parkDriveOutPre(CameraData cameraData, EquipGateInfo equipGateInfo) throws Exception {
        if (Constant.REGION_TYPE_1.equals(equipGateInfo.getRegionType())) {
            // 嵌套管控区域驶离
            return parkDriveOutNested(cameraData, equipGateInfo);
        } else {
            // 独立区域，按正常的流程走
            return parkDriveOut(cameraData, equipGateInfo);
        }
    }

    /**
     * 嵌套区域驶离
     *
     * @Author songqiang
     * @Description
     * @Date 2021/8/2 17:31
     **/
    public String parkDriveOutNested(CameraData cameraData, EquipGateInfo equipGateInfo) throws Exception {
        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");

        String plateNo = cameraData.getLicense();// 车牌
        int plateType = cameraData.getPlateType();// 车牌类型
        if (StringUtils.isEmpty(plateNo)) {// 车牌号为空
            log.error("【驶离】--车牌号为空");
            ResponseResult.SUCCESS().toString();
        }

        // 开闸
        GateMsg gateMsg = new GateMsg();
        gateMsg.setDevicecode(equipGateInfo.getDevicecode());
        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);

        // 驶离，下发语音
        if (isVoice) {
            // 发送车辆驶入消息
            // voiceType = 5
            // ledType = 7
            Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 5, 7, null, 1000, 0));
            thread.start();
        }

        // 车位数+1
        parkService.updateBerthCount(1, equipGateInfo.getRegionCode(), plateNo, String.valueOf(plateType));

        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 小停车场驶离
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/2 9:02
     **/
    public String parkDriveOut(CameraData cameraData, EquipGateInfo equipGateInfo) throws Exception {
        // 1.获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String isEtcStr = properties.getProperty("is_etc");
        String etcUrl = properties.getProperty("etc_url");

        // 2.ETC扣费配置
        EtcBean etcBean = new EtcBean();
        if ("1".equals(isEtcStr)) {
            etcBean.setOnEtc(true);
        } else {
            etcBean.setOnEtc(false);
        }
        etcBean.setEtcUrl(etcUrl);
        String plateNo = cameraData.getLicense();// 车牌
        int plateType = cameraData.getPlateType();// 车牌类型
        if (StringUtils.isEmpty(plateNo)) {// 车牌号为空
            log.error("【驶离】--车牌号为空");
            ResponseResult.SUCCESS().toString();
        }

        // 3.vip中p
        List<SysCarConfig> list_car_config = sysCarConfigRepository.findSysCarConfig(plateNo, String.valueOf(plateType));
        if (list_car_config.size() > 0) {
            // 放行
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(equipGateInfo.getDevicecode());
            // 写缓存
            ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
            return ResponseResult.SUCCESS().toString();
        }

        // 4.查询车牌VIP信息
        // --- 此处查询vip信息，是为了，使用普通白名单的系统，在车辆入场之后才办会员，办完会员之后能免费出场
        // --- 客户白名单则不能查询vip信息，因为出场的时候，不知道客户下其他正在进行的车辆是在场还是离场状态
//        List<ParkVipBean> list_vip = parkVipInfoMapper.findParkVipInfo(plateNo, String.valueOf(plateType), DateUtil.toDate("yyyy-MM-dd"));
        ParkVipBean parkVipBean = null;
//        if (list_vip.size() > 0) {
//            parkVipBean = list_vip.get(0);
//        }
        String whiteListType = properties.getProperty("white_list_type");
        // 只有普通白名单，才再次获取一次白名单信息
        if ("1".equals(whiteListType)) {
            parkVipBean = this.getVipInfo(plateNo, String.valueOf(plateType));
        }

        // 5.通行方式
        if ("1".equals(equipGateInfo.getPointFunc())) {// 只白名单放行
            boolean isTscl = false;
            // 特殊车辆放行
            isTscl = this.isTscl(cameraData);
            if (null == parkVipBean && !isTscl) {// 不放行，下发语音屏显，不开闸，判断条件非会员，不是特殊车辆
                int voiceType = 10;
                int ledType = 10;
                Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                thread.start();
                return ResponseResult.SUCCESS().toString();
            }
        }

        // 6.上传图片
        String imgPath = "";// 图片路径
        // 图片上传
        imgPath = this.uploadImgOne(cameraData);
        int colorType = Constant.convertColorType4OpenEpark(cameraData.getColorType());
        // 车牌类型转换（epark使用）
        String eparkType = Constant.convertPlateOpenEpark4Epark(colorType);
        // 7.插入出场记录
        ParkDetectInfo parkDetectInfo = this.saveDetectInfo(plateNo, plateType, eparkType, imgPath, Constant.DRIVE_OUT_CODE, equipGateInfo, cameraData, "");
        // 8.根据车牌号查询订单，（查询条件：车牌类型和车牌号码查询）
        List<ParkChargeInfo> list_parkChargeInfo = orderservice.findOutCarOrder(plateNo, String.valueOf(plateType));

        // 8.1.查询到不到订单，说明识别错误，不知是入场识别错误还是出场识别错误，
        // 8.2.根据配置，判断是直接免费放行，
        // 8.3.根据配置，是否添加异常数据
        if (list_parkChargeInfo.size() <= 0) {
            log.error("【驶离】--【" + plateNo + "】未查询到订单信息");
            // 更新驶离信息
            parkDetectInfo.setOutType(Constant.UNNORMAL_OUT);
            parkDetectInfoRepository.save(parkDetectInfo);

            // 系统配置无订单不放行
            String noinNoout = properties.getProperty("noin_noout");
            // 无驶入驶离不放行开关打开
            // 不是会员
            // 不是特殊车辆
            if ("1".equals(noinNoout) && null == parkVipBean && !this.isTscl(cameraData)) {
                int voiceType = 9;
                int ledType = 13;
                Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), voiceType, ledType, null, 1000, 0));
                thread.start();
                return ResponseResult.SUCCESS().toString();
            }

            // 系统配置异常驶离控制
            String abnormalFlag = properties.getProperty("abnormal_out");
            if ("1".equals(abnormalFlag)) {
                // 保存异常驶离记录
                AbnormalOut abnormalOut = abnormalOutRepository.findAbnormalOutByCar(plateNo, String.valueOf(plateType));
                if (null != abnormalOut) {
                    int num = abnormalOut.getCumulNum();
                    abnormalOut.setCumulNum(num + 1);
                    abnormalOutRepository.save(abnormalOut);
                } else {
                    abnormalOut = new AbnormalOut();
                    abnormalOut.setPlateNo(plateNo);
                    abnormalOut.setPlateType(String.valueOf(plateType));
                    abnormalOut.setCumulNum(1);
                    abnormalOutRepository.save(abnormalOut);
                }
                // 保存异常驶离详细记录
                AbnormalOutDetail detail = new AbnormalOutDetail();
                detail.setPlateNo(plateNo);
                detail.setPlateType(String.valueOf(plateType));
                detail.setOutTime(parkDetectInfo.getCollectiondate());// 过车时间
                detail.setImgUrl(parkDetectInfo.getCpic1path());// 过车图片地址
                detail.setAid(abnormalOut.getId());
                abnormalOutDetailRepository.save(detail);
            }

            // 直接放行
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(equipGateInfo.getDevicecode());
            // 写缓存放行
            ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
            return ResponseResult.SUCCESS().toString();
        }

        // 获取时间最近的一条订单
        ParkChargeInfo parkChargeInfo = list_parkChargeInfo.get(0);
        // vip信息
        String vipId = parkChargeInfo.getVipId();
        // 其他订单异常结束
        if (list_parkChargeInfo.size() > 1) {
            for (int i = 1; i < list_parkChargeInfo.size(); i++) {
                ParkChargeInfo parkChargeInfoTmp = list_parkChargeInfo.get(i);
                parkChargeInfoTmp.setExitType(Constant.EXIT_TYPE_EXCEPTION);
                parkChargeInfoTmp.setCollectiondate2(parkChargeInfoTmp.getCollectiondate1());
                parkChargeInfoTmp.setParkDuration(0);
                parkChargeInfoTmp.setTotalcharge(0f);
                if (parkChargeInfoTmp.getCharge() <= 0) {
                    parkChargeInfoTmp.setCharge(0);
                    parkChargeInfoTmp.setRealcharge(0f);
                    parkChargeInfoTmp.setDerateFee(0);
                    parkChargeInfoTmp.setCouponFee(0);
                    parkChargeInfoTmp.setDiscount(0);
                }
                parkChargeInfoTmp.setOperator("0");
                parkChargeInfoTmp.setOutUpload(0);// 驶离上传标志
                parkChargeInfoTmp.setPayType(Constant.PAY_TYPE_CASH);
                parkChargeInfoTmp.setPayTime(new Date());
                parkChargeInfoRepository.saveAndFlush(parkChargeInfoTmp);
                // 上传订单驶离，修改订单信息
                Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfoTmp, null, "8102"));
                upLoadDriveOutThread.start();
            }
        }
        // 查询在场车辆信息
        List<ParkInnerCars> list_inner_cars = parkInnerCarsRepository.findParInnerCars(plateNo, String.valueOf(plateType));
        // 删除在场车辆信息
        for (ParkInnerCars parkInnerCars : list_inner_cars) {
            parkInnerCarsRepository.delete(parkInnerCars);
        }
        // 更新停车场车位信息
        parkService.updateBerthCount(list_inner_cars.size(), equipGateInfo.getRegionCode(), plateNo, String.valueOf(plateType));

        ////////////////////////////////////计费开始////////////////////////////////////////////////////
        // 获取订单费用
        boolean isFreeTime = ParkCommon.getIsFreeTime(parkChargeInfo);
        Date TimeIn = parkChargeInfo.getCollectiondate1();// 驶入时间
        Date TimeOut = cameraData.getInsertTime();// 驶离时间
        List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(parkChargeInfo.getDevicecode());
        String regionCode = list_equip.get(0).getRegionCode();
        ParkingInfo parkingInfo = parkService.getParkingFee(TimeIn, TimeOut, plateType, isFreeTime, regionCode);
        double parkingFee = parkingInfo.getCost();// 停车费用
        int parkLength = parkingInfo.getParkLength();// 停车时长

        // 判断车辆是否是白名单车辆（现阶段只支持白名单）
        if (StringUtils.isNotEmpty(vipId) || null != parkVipBean) {
            return this.whiteListOut(parkChargeInfo, equipGateInfo, parkDetectInfo, cameraData, parkVipBean, parkLength, imgPath, plateNo);
        }

        // 判断是否平台故障，系统设置里
        String playformFailure = properties.getProperty("playform_failure");// playform_failure

        if (parkingFee <= 0 || "1".equals(playformFailure)) {// 停车费用为0，或者平台故障
            return this.zeroFreeOut(parkChargeInfo, equipGateInfo, parkDetectInfo, cameraData, parkLength, imgPath, plateNo);
        }

        float charge = parkChargeInfo.getCharge();// 订单费用
        // float derate_fee = parkChargeInfo.getDerateFee();// 折扣费用
        // 折扣费用一旦产生不能清除，折扣暂时是客户端操作的，暂时出场时的折扣费用变为0
        // 识别出场时，将折扣费用置为0
        float derate_fee = 0f;
        float coupon_fee = parkChargeInfo.getCouponFee();// 优惠券费用
        float has_paid = charge + derate_fee + coupon_fee;// 已经支付的费用
        if (has_paid > 0) {// 已经支付过费用了
            if (has_paid >= parkingFee) {
                this.hasPaidGo(parkChargeInfo, equipGateInfo, parkDetectInfo, cameraData, parkLength, parkingFee, derate_fee, coupon_fee, imgPath, charge);
            } else {
                boolean isFree = false;
                double parkingFeeTmp = 0;
                // 判断是否开启场内付
                String innerPay = properties.getProperty("inner_pay");
                if (innerPay.equals("1")) {// 开启场内付
                    // 获取场内付免费时长
                    String innerPayFreeTime = properties.getProperty("inner_pay_free_time");
                    // 判断是否符合规定时间内放行条件
                    isFree = this.isHaspaidGoFree(parkChargeInfo, parkingInfo, innerPayFreeTime);
                    // 符合放行条件，放行+数据上传+返回
                    if (isFree) {
                        return this.hasPaidGo(parkChargeInfo, equipGateInfo, parkDetectInfo, cameraData, parkLength, parkingFee, derate_fee, coupon_fee, imgPath, charge);
                    }
                }
                // 完成剩余费用支付
                this.complatePaid(parkChargeInfo, equipGateInfo, cameraData, parkingInfo, parkDetectInfo, etcBean, parkingFee, has_paid, parkLength, derate_fee, coupon_fee, imgPath);
            }
        } else {// 没有支付过费用
            // 更新epark会员自动支付完成信息
            parkChargeInfo = this.updateDirveOutFeeOrder(parkLength, parkingFee, 0, imgPath, parkChargeInfo, equipGateInfo, Constant.EXIT_TYPE_TMP, parkDetectInfo, true, cameraData);
            // =============================支付开始==========================================
            if (isEpark) {
                // epark会员支付线程，小线程开起来
                Thread eparkPayThread = new Thread((Runnable) new EparkPayThread(parkChargeInfo, parkingFee, equipGateInfo, cameraData, isVoice, parkingInfo, etcBean));
                eparkPayThread.start();
            }
        }

        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 解析相机json数据
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/2 8:48
     **/
    private CameraData analysisCameraDate(String jsonStr) {
        CameraData cameraData = new CameraData();
        JSONObject jb = JSONObject.fromObject(jsonStr);

        JSONObject infoJb = jb.getJSONObject("AlarmInfoPlate");

        String serialNo = infoJb.getString("serialno");
        String deviceName = infoJb.getString("deviceName");
        String ipAddr = infoJb.getString("ipaddr");

        cameraData.setSerialNo(serialNo);
        cameraData.setDeviceName(deviceName);
        cameraData.setIpAddr(ipAddr);

        JSONObject plateJb = infoJb.getJSONObject("result").getJSONObject("PlateResult");
        int colorType = plateJb.getInt("colorType");
        int plateType = plateJb.getInt("type");
        int confidence = plateJb.getInt("confidence");
        int direction = plateJb.getInt("direction");
        String location = plateJb.getJSONObject("location").toString();
        String locationF = "";
        if (StringUtils.isNotEmpty(location)) {// 位置数据转换
            JSONObject jbLo = JSONObject.fromObject(location);
            locationF = locationF + jbLo.getJSONObject("RECT").getInt("bottom");
            locationF = locationF + jbLo.getJSONObject("RECT").getInt("left");
            locationF = locationF + jbLo.getJSONObject("RECT").getInt("right");
            locationF = locationF + jbLo.getJSONObject("RECT").getInt("top");
        }
        int timeUsed = plateJb.getInt("timeUsed");
        String timeStamp = plateJb.getJSONObject("timeStamp").toString();
        int triggerType = plateJb.getInt("triggerType");
        String license = plateJb.getString("license");


        int isoffline = plateJb.getInt("isoffline");
        int plateid = plateJb.getInt("plateid");

        cameraData.setColorType(colorType);
        cameraData.setPlateType(plateType);
        cameraData.setConfidence(confidence);
        cameraData.setDirection(direction);
        cameraData.setLocation(locationF);
        cameraData.setTimeUsed(timeUsed);
        cameraData.setTimeStamp(timeStamp);
        cameraData.setTriggerType(triggerType);
        cameraData.setLicense(license);
        cameraData.setIsoffline(isoffline);
        cameraData.setPlateid(plateid);

        // 判断车牌防伪节点是否存在
        if (plateJb.containsKey("is_fake_plate")) {
            String isFakePlate = plateJb.getString("is_fake_plate");
            cameraData.setIsFakePlate(isFakePlate);
        }
        // 判断大图是否存在
        if (plateJb.containsKey("imageFile")) {
            String imageFile = plateJb.getString("imageFile");
            int imageFileLen = plateJb.getInt("imageFileLen");
            if (StringUtils.isNotEmpty(imageFile)) {
                cameraData.setImageFile(Base64Utils.decodeFromString(imageFile));
            }
            cameraData.setImageFileLen(imageFileLen);
        }

        // 判断小图是否存在
        if (plateJb.containsKey("imageFragmentFile")) {
            String imageFragmentFile = plateJb.getString("imageFragmentFile");
            int imageFragmentFileLen = plateJb.getInt("imageFragmentFileLen");
            if (StringUtils.isNotEmpty(imageFragmentFile)) {
                cameraData.setImageFragmentFile(Base64Utils.decodeFromString(imageFragmentFile));
            }
            cameraData.setImageFragmentFileLen(imageFragmentFileLen);
        }

        cameraData.setInsertTime(new Date());

        // 保存相机原始数据
        if (cameraData.getTriggerType() != 4) {
            cameraDataRepository.saveAndFlush(cameraData);
        }

        return cameraData;
    }

    /**
     * 图片上传方法（单张，返回图片路径）
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 10:04
     **/
    public String uploadImgOne(CameraData cameraData) {

        // 从缓存中获取图片上传路径、后缀信息
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String fileRoot = properties.getProperty("file_upload_root");
        if (!(fileRoot.endsWith("\\") || fileRoot.endsWith("/"))) {
            // 判断改路径是否携带"\"，若没有，则添加
            if (fileRoot.endsWith("\\")) {
                fileRoot = fileRoot + "\\";
            } else {
                fileRoot = fileRoot + "/";
            }
        }

        String suffix = properties.getProperty("file_upload_suffix");

        String imgPath = "";
        try {
            if (null != cameraData.getImageFile()) {
                String imgBase64Str = Base64Utils.encodeToString(cameraData.getImageFile());
                String[] images = new String[1];
                images[0] = imgBase64Str;
                List<String> imageNames = FileOperateUtil.saveThirdParkBase64Images(fileRoot, images, suffix);
                if (imageNames.size() > 0) {
                    // log.info("【驶入/驶离】--上传图片文件返回路径：" + imageNames.get(0));
                    imgPath = imageNames.get(0);
                }
            }
        } catch (Exception e) {
            log.error("【驶入驶离】---图片上传失败。");
            e.printStackTrace();
            return "";
        }
        return imgPath.replaceAll("\\\\", "/");
    }

    /**
     * 保存驶入驶离记录
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 10:17
     **/
    private ParkDetectInfo saveDetectInfo(String plateNo, int plateType, String
            eparkType, String imgPath, String driveCode, EquipGateInfo equipGateInfo, CameraData
                                                  cameraData, String vipId) {
        // 插入驶入驶离记录
        ParkDetectInfo parkDetectInfo = new ParkDetectInfo();
        parkDetectInfo.setNid(cameraData.getId());
        parkDetectInfo.setLicensetype(String.valueOf(plateType));// 车牌类型
        parkDetectInfo.setVehicleType(eparkType);// epark车辆类型，主要区分蓝牌和黄牌车，因为两种车牌的车牌号有可能重复 add by sq 20200706
        parkDetectInfo.setCarno(plateNo);// 车牌号
        parkDetectInfo.setCarnosecond(plateNo);// 车牌号copy
        parkDetectInfo.setCarnoOriginal(plateNo);// 原始车牌号
        parkDetectInfo.setPointcode(equipGateInfo.getPointcode());// 出入口编号
        parkDetectInfo.setPointname(equipGateInfo.getPointname());// 出入口名称
        parkDetectInfo.setDirection(driveCode);// 方向编号，9进入停车场  10 离开停车场
        parkDetectInfo.setDevicecode(equipGateInfo.getDevicecode());// 相机编号
        parkDetectInfo.setCpic1path(Constant.IMG_SERVER + imgPath);// 图片路径（全路径）
        parkDetectInfo.setCarcolor(Constant.CAR_COLOR);// 车身颜色，预留字段
        parkDetectInfo.setPlatecolor(String.valueOf(cameraData.getColorType()));// 车牌颜色，（重要，上传epark使用） add by sq 20200706
        parkDetectInfo.setPlatePosition(cameraData.getLocation());// 车牌坐标
        parkDetectInfo.setCollectiondate(cameraData.getInsertTime());
        parkDetectInfo.setSavedate(new Date());
        parkDetectInfo.setRegion_code(equipGateInfo.getRegionCode());
        parkDetectInfo.setVipOutnumber(vipId);// vip类型
        // 保存驶入记录
        parkDetectInfoRepository.save(parkDetectInfo);
        return parkDetectInfo;
    }

    /**
     * 更新离场车辆订单
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/7 16:48
     **/
    private ParkChargeInfo updateDirveOutFeeOrder(int parkLength, double totalCharge,
                                                  double realCharge, String imgPath, ParkChargeInfo parkChargeInfo,
                                                  EquipGateInfo equipGateInfo, String exitType, ParkDetectInfo parkDetectInfo,
                                                  boolean isPay, CameraData cameraData) {
        parkChargeInfo.setParkDuration(parkLength);
        parkChargeInfo.setTotalcharge((float) totalCharge);
        parkChargeInfo.setCharge(0);
        parkChargeInfo.setRealcharge((float) realCharge);
        parkChargeInfo.setDerateFee(0);
        parkChargeInfo.setCouponFee(0);
        parkChargeInfo.setDiscount(0);
        parkChargeInfo.setOperator("0");
        parkChargeInfo.setExitType(exitType);// 驶离方式
        parkChargeInfo.setPointcodeExt(equipGateInfo.getPointcode());// 出口编号
        parkChargeInfo.setPointnameExt(equipGateInfo.getPointname());// 出口名称
        parkChargeInfo.setDevicecodeExt(equipGateInfo.getDevicecode());// 摄像机编号
        parkChargeInfo.setCpicExitPath(Constant.IMG_SERVER + imgPath);// 驶离图片
        parkChargeInfo.setExitNid(parkDetectInfo.getNid());// 驶离ID
        parkChargeInfo.setCollectiondate2(cameraData.getInsertTime());// 驶离时间
        parkChargeInfo.setOutUpload(0);// 驶离上传标志
        if (isPay) {// 支付记录上传标志
            parkChargeInfo.setPayUpload(0);
        }
        if (totalCharge <= 0) {
            parkChargeInfo.setPayTime(new Date());
            parkChargeInfo.setPayType(Constant.PAY_TYPE_CASH);// 现金支付
        }
        parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);
        return parkChargeInfo;
    }

    /**
     * 相机comet轮询处理
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/8 8:53
     **/
    public String parkComet(String serialno) throws Exception {

        // 查询所有主设备信息(改进：缓存所有的设备信息，每次遍历去取)
        List<EquipInfo> list_equip = equipInfoRepository.findEquipList();
        EquipInfo equipInfo = this.getEquipInList(serialno, list_equip);

        if (null == equipInfo) {
            throw new Exception("未查询到设备信息");
        }

        String videoFunc = equipInfo.getVideofunc();

        // 读取缓存
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();

        if (Constant.DRIVE_OUT_CODE.equals(videoFunc)) {

        }

        JSONObject jb = new JSONObject();
        JSONObject jbSub = new JSONObject();

        // 开闸消息
        GateMsg gateMsg = (GateMsg) ehcacheUtil.get(Constant.PARK_CACHE, "gate_" + equipInfo.getDevicecode());
        if (null != gateMsg) {// 有开闸信息

            // 缓存记录出口开闸时间
            Date lastOpenTime = (Date) ehcacheUtil.get(Constant.PARK_CACHE, "out_time" + equipInfo.getDevicecode());
            if (null != lastOpenTime) {
                // 判断上次开闸时间，与现在时间的时间间隔
                long timeLen = new Date().getTime() - lastOpenTime.getTime();
                if (timeLen <= 1000) {
                    log.info("上次开闸距现在不足1秒");
                    Thread.sleep(5000);// 线程休眠5秒
                }
                if (timeLen > 1000 && timeLen <= 2500) {
                    log.info("上次开闸距现在不足2.5秒");
                    Thread.sleep(3000);// 线程休眠3秒
                }
                if (timeLen > 2500 && timeLen <= 5000) {
                    log.info("上次开闸距现在不足4秒");
                    Thread.sleep(2000);// 线程休眠2秒
                }
            }

            log.info("设备：" + equipInfo.getDevicecode() + "获得开闸信号。" + DateUtil.formatDateYMDHMS(new Date()));
            jbSub.put("info", "ok");
            // 删除缓存
            ehcacheUtil.remove(Constant.PARK_CACHE, "gate_" + equipInfo.getDevicecode());
            // 设备开闸时间
            if (Constant.DRIVE_OUT_CODE.equals(videoFunc)) {
                // 缓存记录出口开闸时间
                ehcacheUtil.put(Constant.PARK_CACHE, "out_time" + equipInfo.getDevicecode(), new Date());
            }
        }
        // 语音播报消息
        LedAndVoiceMsg ledAndVoiceMsg = (LedAndVoiceMsg) ehcacheUtil.get(Constant.PARK_CACHE, "led_voice_" + equipInfo.getDevicecode());
        if (null != ledAndVoiceMsg) {
            log.info("设备：" + equipInfo.getDevicecode() + "获得led和语音信号。" + DateUtil.formatDateYMDHMS(new Date()));
            JSONArray ja = new JSONArray();
            JSONObject jb1 = new JSONObject();
            jb1.put("serialChannel", ledAndVoiceMsg.getChannel());
            jb1.put("data", ledAndVoiceMsg.getContent());
            jb1.put("dataLen", ledAndVoiceMsg.getDataLength());
            ja.add(jb1);
            jbSub.put("serialData", ja);
            // 删除缓存
            ehcacheUtil.remove(Constant.PARK_CACHE, "led_voice_" + equipInfo.getDevicecode());
        }

        // 手动触发识别消息
        GateMsg manualMsg = (GateMsg) ehcacheUtil.get(Constant.PARK_CACHE, "manual_trigger_" + equipInfo.getDevicecode());
        if (null != manualMsg) {
            jbSub.put("manualTrigger", "ok");
            // 删除缓存
            ehcacheUtil.remove(Constant.PARK_CACHE, "manual_trigger_" + equipInfo.getDevicecode());
        }

        // 更新时间
        LedAndVoiceMsg dateMsg = (LedAndVoiceMsg) ehcacheUtil.get(Constant.PARK_CACHE, "update_time_" + equipInfo.getDevicecode());
        if (null != dateMsg) {
            // log.info("设备：" + equipInfo.getDevicecode() + "获得更新信号。" + DateUtil.formatDateYMDHMS(new Date()));
            JSONArray ja = new JSONArray();
            JSONObject jb1 = new JSONObject();
            jb1.put("serialChannel", dateMsg.getChannel());
            jb1.put("data", dateMsg.getContent());
            jb1.put("dataLen", dateMsg.getDataLength());
            ja.add(jb1);
            jbSub.put("serialData", ja);
            // 删除缓存
            ehcacheUtil.remove(Constant.PARK_CACHE, "update_time_" + equipInfo.getDevicecode());
        }

        jb.put("Response_AlarmInfoPlate", jbSub);
        return jb.toString();
    }

    /**
     * 根据设备编号，在设备列表中便利设备，设备列表暂存在缓存中
     *
     * @Author songqiang
     * @Description
     * @Date 2020/8/21 8:56
     **/
    private EquipInfo getEquipInList(String serialno, List<EquipInfo> list_equip) {
        EquipInfo equipInfo = null;
        for (EquipInfo bean : list_equip) {
            if (serialno.equals(bean.getDevicecode())) {
                equipInfo = bean;
                break;
            }
        }
        return equipInfo;
    }

    /**
     * 更新设备时间
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/4 11:15
     **/
    public void updateDeviceTime() {
        // 获取所有设备信息
        List<EquipInfo> list_equip = equipInfoRepository.findEquipList();
        // 循环设备更新设备信息
        for (EquipInfo equipInfo : list_equip) {
            // 获取更新的时间串
            Map map = VoiceLedUtil.getUpTimeStr(new Date());
            if ((int) map.get("length") > 0) {
                int length = (int) map.get("length");
                String baseStr = (String) map.get("baseStr");
                EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
                LedAndVoiceMsg ledAndVoiceMsg = new LedAndVoiceMsg();
                ledAndVoiceMsg.setDataLength(length);
                ledAndVoiceMsg.setContent(baseStr);
                ledAndVoiceMsg.setChannel(equipInfo.getVoiceChannel());
                ledAndVoiceMsg.setDevicecode(equipInfo.getDevicecode());
                ehcacheUtil.put(Constant.PARK_CACHE, "update_time_" + equipInfo.getDevicecode(), ledAndVoiceMsg);
            }
        }
    }

    /**
     * 场内支付或提前支付放行
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/1 11:59
     **/
    private String hasPaidGo(ParkChargeInfo parkChargeInfo, EquipGateInfo
            equipGateInfo, ParkDetectInfo parkDetectInfo,
                             CameraData cameraData, int parkLength, double parkingFee, float derate_fee,
                             float coupon_fee, String imgPath, float charge) throws IOException {

        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");

        // 1.支付的费用满足驶离要求，开闸驶离，更新订单信息
        parkChargeInfo.setParkDuration(parkLength);
        // 按实际支付的费用作为订单总费用，因为是在场内支付后，免费放行的时间内
        // mod by sq 2020-12-03
        parkChargeInfo.setTotalcharge(charge);
        // parkChargeInfo.setCharge(has_paid);
        parkChargeInfo.setRealcharge(0);
        parkChargeInfo.setDerateFee(derate_fee);
        parkChargeInfo.setCouponFee(coupon_fee);
        parkChargeInfo.setDiscount(0);
        parkChargeInfo.setOperator("0");
        parkChargeInfo.setExitType(Constant.EXIT_TYPE_AUTO);// 自动驶离
        parkChargeInfo.setPointcodeExt(equipGateInfo.getPointcode());// 出口编号
        parkChargeInfo.setPointnameExt(equipGateInfo.getPointname());// 出口名称
        parkChargeInfo.setDevicecodeExt(equipGateInfo.getDevicecode());// 摄像机编号
        parkChargeInfo.setCpicExitPath(Constant.IMG_SERVER + imgPath);// 驶离图片
        parkChargeInfo.setExitNid(parkDetectInfo.getNid());// 驶离ID
        parkChargeInfo.setCollectiondate2(cameraData.getInsertTime());// 驶离时间
        parkChargeInfoRepository.saveAndFlush(parkChargeInfo);// 保存订单

        // 桌面推送
        String isDesk = properties.getProperty("is_desk");
        if ("1".equals(isDesk)) {
            WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_PAY_MESSAGE, "场内支付", String.valueOf(charge));
        }

        // 2.放行
        // 开闸消息插入缓存
        GateMsg gateMsg = new GateMsg();
        gateMsg.setDevicecode(equipGateInfo.getDevicecode());
        // 写缓存
        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
        // 3.播报语音
        if (isVoice) {// 是否下发语音
            // 下发语音和LED屏显消息
            // voiceType = 7
            // ledType = 4
            VoiceLedThread thread = new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 7, 4, null, 1000, charge);
            thread.run();
        }
        // 4.上传驶离
        Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, cameraData, "8102"));
        upLoadDriveOutThread.start();
        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 判断是否符合场内付免费条件
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/1 13:06
     **/
    private boolean isHaspaidGoFree(ParkChargeInfo parkChargeInfo, ParkingInfo
            parkingInfo, String innerPayFreeTime) {
        boolean isFree = false;
        int freeTime = 0;
        try {
            freeTime = Integer.parseInt(innerPayFreeTime);
        } catch (NumberFormatException e) {
            log.error("场内付免费时长转换整数失败");
            e.printStackTrace();
        }
        // 根据订单编号，查询订单最后支付时间
        List<OrderPay> listPay = orderPayRepository.findOrderPayByOrderNo(parkChargeInfo.getNid());
        if (listPay.size() > 0) {
            OrderPay orderPay = listPay.get(0);
            // 获取最后支付时间
            DateTime last_paid = new DateTime(orderPay.getPayTime());
            // 驶离时间
            DateTime dirveOutTime = parkingInfo.getDriveOut();
            int freeLength = (int) (dirveOutTime.getMillis() - last_paid.getMillis()) / 60000;
            if (freeLength < freeTime) {
                isFree = true;
            }
        }
        return isFree;
    }

    /**
     * 白名单车辆放行
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/1 13:23
     **/
    private String whiteListOut(ParkChargeInfo parkChargeInfo, EquipGateInfo
            equipGateInfo, ParkDetectInfo parkDetectInfo,
                                CameraData cameraData, ParkVipBean parkVipBean, int parkLength, String
                                        imgPath, String plateNo) throws IOException {

        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");

        // 更新白名单订单
        parkChargeInfo = this.updateDirveOutFeeOrder(parkLength, 0, 0, imgPath, parkChargeInfo, equipGateInfo, Constant.EXIT_TYPE_FREE, parkDetectInfo, true, cameraData);
        // 上传驶离、支付记录
        if (isEpark) {
            Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, cameraData, "8102"));
            upLoadDriveOutThread.start();
            // 上传支付记录
            Thread upLoadPayDetailThread = new Thread((Runnable) new UpLoadPayDetailThread(parkChargeInfo, null, cameraData));
            upLoadPayDetailThread.start();
        }
        // 播报语音
        if (isVoice) {
            // 发送车辆驶入消息
            // voiceType = 6
            // ledType = 7
            Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 6, 7, parkVipBean, 1000, 0));
            thread.start();
        }

        // 桌面推送
        String isDesk = properties.getProperty("is_desk");
        if ("1".equals(isDesk)) {
            WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_PAY_MESSAGE, "白名单车辆", "0");
        }

        // 放行
        // 开闸消息插入缓存
        GateMsg gateMsg = new GateMsg();
        gateMsg.setDevicecode(equipGateInfo.getDevicecode());
        // 写缓存
        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);

        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 0元订单放行
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/1 13:35
     **/
    private String zeroFreeOut(ParkChargeInfo parkChargeInfo, EquipGateInfo
            equipGateInfo, ParkDetectInfo parkDetectInfo, CameraData cameraData, int parkLength, String
                                       imgPath, String plateNo) throws IOException {

        // 获取缓存读取工具
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 读取ETC配置
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");

        // 更新0费用订单
        parkChargeInfo = this.updateDirveOutFeeOrder(parkLength, 0, 0, imgPath, parkChargeInfo, equipGateInfo, Constant.EXIT_TYPE_FREE, parkDetectInfo, true, cameraData);

        // 上传驶离、支付记录
        if (isEpark) {
            Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, cameraData, "8102"));
            upLoadDriveOutThread.start();
            // 上传支付记录
            Thread upLoadPayDetailThread = new Thread((Runnable) new UpLoadPayDetailThread(parkChargeInfo, null, cameraData));
            upLoadPayDetailThread.start();
        }
        // 播报语音
        if (isVoice) {
            // 发送车辆驶入消息
            // voiceType = 6
            // ledType = 7
            Thread thread = new Thread((Runnable) new VoiceLedThread(plateNo, equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 6, 7, null, 1000, 0));
            thread.start();
        }

        // 桌面推送
        String isDesk = properties.getProperty("is_desk");
        if ("1".equals(isDesk)) {
            WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_PAY_MESSAGE, "免费放行", "0");
        }

        // 放行
        GateMsg gateMsg = new GateMsg();
        gateMsg.setDevicecode(equipGateInfo.getDevicecode());
        // 写缓存
        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);

        return ResponseResult.SUCCESS().toString();
    }

    /**
     * 完成剩余费用支付
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/1 13:44
     **/
    private void complatePaid(ParkChargeInfo parkChargeInfo, EquipGateInfo
            equipGateInfo, CameraData cameraData, ParkingInfo parkingInfo, ParkDetectInfo
                                      parkDetectInfo,
                              EtcBean etcBean, double parkingFee, double has_paid, int parkLength,
                              float derate_fee, float coupon_fee, String imgPath) {
        double parkingFeeTmp = parkingFee - (double) has_paid;// 仍需支付的费用

        parkChargeInfo.setParkDuration(parkLength);
        parkChargeInfo.setTotalcharge((float) parkingFee);
        parkChargeInfo.setRealcharge(0);
        parkChargeInfo.setDerateFee(derate_fee);
        parkChargeInfo.setCouponFee(coupon_fee);
        parkChargeInfo.setDiscount(0);
        parkChargeInfo.setOperator("0");
        parkChargeInfo.setExitType(Constant.EXIT_TYPE_TMP);// 自动驶离
        parkChargeInfo.setPointcodeExt(equipGateInfo.getPointcode());// 出口编号
        parkChargeInfo.setPointnameExt(equipGateInfo.getPointname());// 出口名称
        parkChargeInfo.setDevicecodeExt(equipGateInfo.getDevicecode());// 摄像机编号
        parkChargeInfo.setCpicExitPath(Constant.IMG_SERVER + imgPath);// 驶离图片
        parkChargeInfo.setExitNid(parkDetectInfo.getNid());// 驶离ID
        parkChargeInfo.setCollectiondate2(cameraData.getInsertTime());// 驶离时间
        parkChargeInfoRepository.saveAndFlush(parkChargeInfo);// 保存订单

        if (isEpark) {
            // epark会员支付线程，小线程开起来
            Thread eparkPayThread = new Thread((Runnable) new EparkPayThread(parkChargeInfo, parkingFeeTmp, equipGateInfo, cameraData, isVoice, parkingInfo, etcBean));
            eparkPayThread.start();
        }
    }

    /**
     * 怕判断是否特殊车辆
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 14:58
     **/
    private boolean isTscl(CameraData cameraData) {
        // 特殊车辆放行
        if (5 == cameraData.getPlateType() || 6 == cameraData.getPlateType() || 8 == cameraData.getPlateType() || 9 == cameraData.getPlateType() ||
                16 == cameraData.getPlateType() || 17 == cameraData.getPlateType()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询白名单信息
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/7 10:32
     **/
    public ParkVipBean getVipInfo(String plateNo, String plateType) {
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String whiteListType = properties.getProperty("white_list_type");
        // 数据库sys_config中未配置时，赋值普通白名单
        if (null == whiteListType || StringUtils.isEmpty(whiteListType)) {
            whiteListType = "1";
        }
        // 普通白名单
        if ("1".equals(whiteListType)) {
            List<ParkVipBean> list_vip = parkVipInfoMapper.findParkVipInfo(plateNo, plateType, DateUtil.toDate("yyyy-MM-dd"));
            if (list_vip.size() > 0) {
                return list_vip.get(0);
            } else {
                return null;
            }
        } else {// 客户白名单
            List<ParkVipBean> list_vip = parkVipInfoMapper.findParkVipInfo2(plateNo, plateType, DateUtil.toDate("yyyy-MM-dd"));
            if (list_vip.size() <= 0) {// 没有白名单信息，临时车直接返回
                return null;
            } else {// 查询客户白名单信息
                String whiteListSize = properties.getProperty("white_list_size");
                if (StringUtils.isEmpty(whiteListSize) || Integer.valueOf(whiteListSize) == 0) {// 判断系统配置的客户下允许同时在场的订单数，是不是全部允许免费
                    return list_vip.get(0);
                } else {// 只允许部分车辆免费
                    ParkVipBean parkVipBean = list_vip.get(0);
                    // 查询客户下所有的白名单车辆
                    List<ParkLeaguerCars> carsList = parkLeaguerCarsRepository.findByLeaguerId(parkVipBean.getVipId());
                    if (carsList.size() <= 1) {// 客户下只有一辆车，那就直接返回白名单信息
                        return parkVipBean;
                    } else {
                        // 查询正在进行的客户订单
                        Integer orderingSize = 0;
                        for (ParkLeaguerCars car : carsList) {
                            if (!(car.getPlateNo().equals(plateNo) && car.getPlateType().equals(plateType))) {// 不是当前正在在入场的车辆
                                List<ParkChargeInfo> orderList = parkChargeInfoRepository.findOutCarOrder(car.getPlateNo(), car.getPlateType());
                                if (orderList.size() > 0) {
                                    ++orderingSize;
                                }
                            }
                        }
                        // log.info("正在进行的订单数：" + orderingSize);
                        if (orderingSize > 0) {// 有其他正在进行的订单
                            // 判断当前订单数和允许白名单的订单数
                            if (Integer.valueOf(whiteListSize) > orderingSize) {// 客户正在进行的车辆订单数，小于允许的白名单数量
                                return parkVipBean;
                            } else {
                                return null;
                            }
                        } else {// 没有订单
                            return parkVipBean;
                        }
                    }
                }

            }
        }
    }

}
