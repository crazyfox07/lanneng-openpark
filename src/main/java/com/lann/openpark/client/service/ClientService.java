package com.lann.openpark.client.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lann.openpark.camera.bean.EquipBean;
import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.dao.entiy.*;
import com.lann.openpark.camera.dao.mapper.BlackInfoMapper;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.mapper.ParkVipInfoMapper;
import com.lann.openpark.camera.dao.repository.*;
import com.lann.openpark.camera.service.CameraService;
import com.lann.openpark.camera.threads.UpLoadDriveOutThread;
import com.lann.openpark.camera.threads.UpLoadPayDetailThread;
import com.lann.openpark.camera.threads.VoiceLedThread;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.bizobj.Rule;
import com.lann.openpark.charge.bizobj.config.*;
import com.lann.openpark.charge.dao.bean.ChargePlanParams;
import com.lann.openpark.charge.dao.entiy.Charge2Policy;
import com.lann.openpark.charge.dao.entiy.ChargePlan;
import com.lann.openpark.charge.dao.entiy.ChargePlanConfig;
import com.lann.openpark.charge.dao.entiy.ChargePlanRegion;
import com.lann.openpark.charge.dao.repository.Charge2PolicyRepository;
import com.lann.openpark.charge.dao.repository.ChargePlanConfigRepository;
import com.lann.openpark.charge.dao.repository.ChargePlanRegionRepository;
import com.lann.openpark.charge.dao.repository.ChargePlanRepository;
import com.lann.openpark.charge.logic.ParkingAlgorithm;
import com.lann.openpark.charge.service.ChargeService;
import com.lann.openpark.client.bean.ParkInnerCars;
import com.lann.openpark.client.bean.*;
import com.lann.openpark.client.dao.entiy.*;
import com.lann.openpark.client.dao.mapper.GateInfoMapper;
import com.lann.openpark.client.dao.mapper.ParkUserInfoMapper;
import com.lann.openpark.client.dao.repository.*;
import com.lann.openpark.client.threads.GetPicThread;
import com.lann.openpark.common.Constant;
import com.lann.openpark.common.enums.ResultEnum;
import com.lann.openpark.common.vo.ResultVO;
import com.lann.openpark.openepark.dao.entiy.OrderPay;
import com.lann.openpark.openepark.dao.repository.OrderPayRepository;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.mapper.ParkChargeInfoMapper;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.order.dao.repository.ParkPayDetailRepository;
import com.lann.openpark.order.service.Orderservice;
import com.lann.openpark.park.dao.mapper.SysConfigMapper;
import com.lann.openpark.park.service.ParkService;
import com.lann.openpark.system.dao.entiy.SysConfig;
import com.lann.openpark.system.dao.repository.SysConfigRepository;
import com.lann.openpark.system.service.SysConfigService;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.JsonDateValueProcessor;
import com.lann.openpark.util.ResultVOUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    ParkUserInfoRepository parkUserInfoRepository;

    @Autowired
    ParkInfoRepository parkInfoRepository;

    @Autowired
    ParkRegionInfoRepository parkRegionInfoRepository;

    @Autowired
    GateInfoRepository gateInoRepository;

    @Autowired
    EquipInfoMapper equipInfoMapper;

    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;

    @Autowired
    OrderPayRepository orderPayRepository;

    @Autowired
    OpenEparkService openEparkService;

    @Autowired
    CameraDataRepository cameraDataRepository;

    @Autowired
    EquipSendMsgRepository equipSendMsgRepository;

    @Autowired
    LedConfigRepository ledConfigRepository;

    @Autowired
    VoiceConfigRepository voiceConfigRepository;

    @Autowired
    ParkDetectInfoRepository parkDetectInfoRepository;

    @Autowired
    ParkInnerCarsRepository parkInnerCarsRepository;

    @Autowired
    ParkPayDetailRepository parkPayDetailRepository;

    @Autowired
    ParkChargeInfoMapper parkChargeInfoMapper;

    @Autowired
    ParkService parkService;

    @Autowired
    GateInfoRepository gateInfoRepository;

    @Autowired
    SysConfigMapper sysConfigMapper;

    @Autowired
    SysConfigService sysConfigService;

    @Autowired
    SysConfigRepository sysConfigRepository;

    @Autowired
    GateInfoMapper gateInfoMapper;

    @Autowired
    EquipInfoRepository equipInfoRepository;

    @Autowired
    ParkVipInfoMapper parkVipInfoMapper;

    @Autowired
    BlackInfoMapper blackInfoMapper;

    @Autowired
    ParkVipInfoRepository parkVipInfoRepository;

    @Autowired
    BlackInfoRepository blackInfoRepository;

    @Autowired
    Charge2PolicyRepository charge2PolicyRepository;

    @Autowired
    ChargePlanRepository chargePlanRepository;

    @Autowired
    ChargePlanConfigRepository chargePlanConfigRepository;

    @Autowired
    Orderservice orderservice;

    @Autowired
    ChargeService chargeService;

    @Autowired
    ChargePlanRegionRepository chargePlanRegionRepository;

    @Autowired
    LogDelInnerCarsRepository logDelInnerCarsRepository;
    @Autowired
    LogPoleControlRepository logPoleControlRepository;
    @Autowired
    LogFreeGoRepository logFreeGoRepository;

    @Autowired
    AbnormalOutRepository abnormalOutRepository;
    @Autowired
    AbnormalOutDetailRepository abnormalOutDetailRepository;

    @Autowired
    ClientMenuRepository clientMenuRepository;
    @Autowired
    ClientRoleRepository clientRoleRepository;
    @Autowired
    RoleLimitRepository roleLimitRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    CarnoLogInfoRepository carnoLogInfoRepository;
    @Autowired
    ParkUserInfoMapper parkUserInfoMapper;
    @Autowired
    ParkLeaguerRepository parkLeaguerRepository;
    @Autowired
    ParkLeaguerCarsRepository parkLeaguerCarsRepository;

    @Autowired
    CameraService cameraService;

    @Value("${parkInfo.isEpark}")
    private boolean isEpark;// ???????????????????????????
    @Value("${parkInfo.isVoice}")
    private boolean isVoice;// ??????????????????

    public static void main(String[] args) {

    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 9:28
     **/
    public String clinetLogin(String userName, String passWord) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // ??????????????????
            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userName);
            if (null == user) {
                jb.put("code", "1");
                jb.put("message", "????????????????????????");
                return jb.toString();
            }
            if (!passWord.equals(user.getPassword())) {
                jb.put("code", "2");
                jb.put("message", "????????????????????????");
                return jb.toString();
            }
            // token
            String uuid = UUID.randomUUID().toString().replace("-", "");
            // ?????????
            ehcacheUtil.put(Constant.PARK_CLIENT, "client_cookie_" + uuid, uuid);
            jb.put("code", "0");
            jb.put("userid", user.getUserid());
            jb.put("message", "????????????");
            jb.put("token", uuid);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 14:05
     **/
    public String parkInfo4Clent() {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // ?????????????????????
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            // ?????????????????????
            ParkInfo parkInfo = parkInfoRepository.findParkInfoById(parkCode);
            if (null == parkInfo) {
                jb.put("code", "1");
                jb.put("message", "???????????????????????????");
                return jb.toString();
            }
            // ??????????????????
            List<ParkRegionInfo> regionList = parkRegionInfoRepository.findParkRegionInfoList();
            if (regionList.size() == 0) {
                jb.put("code", "2");
                jb.put("message", "???????????????????????????");
                return jb.toString();
            }
            // ??????gate??????
            List<GateInfo> gateList = gateInoRepository.findGateInfoList();
            // ??????????????????
            List<EquipBean> equipList = equipInfoMapper.findEquipList();

            JSONObject jbPark = new JSONObject();
            jbPark.put("parkInfo", JSONObject.fromObject(parkInfo));
            jbPark.put("regionList", JSONArray.fromObject(regionList));
            jbPark.put("gateList", JSONArray.fromObject(gateList));
            jbPark.put("equipList", JSONArray.fromObject(equipList));

            jb.put("code", "0");
            jb.put("message", "?????????????????????????????????");
            jb.put("parkConfig", jbPark);
            return jb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/2 16:44
     **/
    public String controlgate(String deviceNo, String userid) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {

            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userid);
            if (null == user) {
                jb.put("code", "1");
                jb.put("message", "??????????????????????????????");
                return jb.toString();
            }

            // ?????????????????????
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(deviceNo);
            // ?????????
            ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + deviceNo, gateMsg);

            // ??????????????????
            EquipInfo equip = equipInfoRepository.findEquipInfoByDevicecode(deviceNo);
            LogPoleControl bean = new LogPoleControl();
            bean.setDeviceCode(deviceNo);
            bean.setDeviceName(equip.getDevicename());
            bean.setLogTime(new Date());
            bean.setUserId(user.getUserid());
            bean.setUserName(user.getUsername());
            logPoleControlRepository.save(bean);

            // ????????????????????????
            Thread getPicThread = new Thread((Runnable) new GetPicThread(deviceNo, bean));
            getPicThread.start();

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/2 16:44
     **/
    public String opengatebyfee(String orderNo, String feeS, boolean isFree, String userid) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {

            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userid);
            if (null == user) {
                jb.put("code", "1");
                jb.put("message", "??????????????????????????????");
                return jb.toString();
            }

            float fee = Float.valueOf(feeS);
            // ????????????????????????????????????
            ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
            if (null == parkChargeInfo) {// ????????????????????????????????????????????????
                JSONObject ret = new JSONObject();
                ret.put("code", "1");
                ret.put("message", "??????????????????");
                ret.put("responseBody", new JSONArray());
                return ret.toString();
            }
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");

            // ????????????????????????
            OrderPay orderPay = new OrderPay();
            orderPay.setParkCode(parkCode);
            orderPay.setOrderNo(orderNo);
            orderPay.setCouponFee(0f);
            orderPay.setDerateFee(0f);
            orderPay.setOutTradeNo("");
            orderPay.setPayFee(fee);
            orderPay.setPayType(Constant.PAY_TYPE_CASH);// ????????????
            orderPay.setPayTime(new Date());// ????????????
            orderPay.setInsertTime(new Date());
            orderPay.setUserId(userid);
            orderPayRepository.save(orderPay);

            // ??????????????????
            float charge = parkChargeInfo.getCharge() + fee;
            parkChargeInfo.setCharge(charge);// ??????????????????
            parkChargeInfo.setPayType(Constant.PAY_TYPE_CASH);
            parkChargeInfo.setPayUpload(0);// ????????????????????????
            parkChargeInfo.setPayTime(new Date());
            parkChargeInfo.setExitType(Constant.MANUAL_FEE);// ??????????????????
            parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);

            // ????????????????????????
            if (isFree) {
                LogFreeGo logFreeGo = new LogFreeGo();
                logFreeGo.setCarno(parkChargeInfo.getCarno());
                logFreeGo.setOrderNo(parkChargeInfo.getNid());
                logFreeGo.setInTime(parkChargeInfo.getCollectiondate1());
                logFreeGo.setOutTime(parkChargeInfo.getCollectiondate2());
                logFreeGo.setLogTime(new Date());
                logFreeGo.setUserId(user.getUserid());
                logFreeGo.setUserName(user.getUsername());
                logFreeGoRepository.save(logFreeGo);
            }

            // ???????????????????????????
            CameraData cameraData = cameraDataRepository.findCameraDataByNid(parkChargeInfo.getExitNid());
            if (isEpark) {
                Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, cameraData, "8102"));
                upLoadDriveOutThread.start();
                // ??????????????????
                Thread upLoadPayDetailThread = new Thread((Runnable) new UpLoadPayDetailThread(parkChargeInfo, orderPay, cameraData));
                upLoadPayDetailThread.start();
            }

            // ?????????????????????
            if (isVoice) {
                List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(parkChargeInfo.getDevicecodeExt());
                if (list_equip.size() > 0) {
                    EquipGateInfo equipGateInfo = list_equip.get(0);
                    GateMsg gateMsg = new GateMsg();
                    gateMsg.setDevicecode(equipGateInfo.getDevicecode());
                    // ?????????
                    ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);
                    // ??????????????????
                    if (isVoice) {
                        // ???????????????LED????????????
                        // voiceType = 7
                        // ledType = 4
                        VoiceLedThread thread = new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 7, 4, null, 1000, charge);
                        thread.run();
                    }

                }
            }

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/3 11:03
     **/
    public String queryOrders(String startDate, String endDate, String startDate1, String endDate1, String plateNo, String orderState, String regionCode, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<OrderInfo> pages = parkChargeInfoMapper.queryOrders(startDate, endDate, plateNo, orderState, regionCode, startDate1, endDate1);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }

    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 9:27
     **/
    public String queryDetectList(String startDate, String endDate, String plateNo, String direction, int pagenum, int pagesize) {

        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<DetectInfo> pages = parkChargeInfoMapper.queryDetectList(startDate, endDate, plateNo, direction);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }

    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 9:27
     **/
    public String queryInnerCarList(String startDate, String endDate, String plateNo, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<ParkInnerCars> pages = parkChargeInfoMapper.queryInnerCarList(startDate, endDate, plateNo);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/15 14:23
     **/
    public String queryPayList(String startDate, String endDate, String plateNo, String payType, String userid, int pagenum, int pagesize) {

        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<OrderPayInfo> pages = parkChargeInfoMapper.queryPayList(startDate, endDate, plateNo, payType, userid);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }


    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 8:42
     **/
    public String updateParkBerth(String berthNumStr) {
        JSONObject jb = new JSONObject();
        try {
            int berthNum = Integer.valueOf(berthNumStr);
            // ???????????????????????????
            // parkService.updateBerthCount(berthNum);
            jb.put("code", "0");
            jb.put("message", "?????????????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 11:16
     **/
    public String updateRegionBerth(String regionCode, String berthNumStr) {
        JSONObject jb = new JSONObject();
        try {
            int berthNum = Integer.valueOf(berthNumStr);
            // ??????????????????
            ParkRegionInfo region = parkRegionInfoRepository.findRegionInfoByRegionCode(regionCode);
            if (null != region) {
                parkService.updateBerthCount(berthNum, regionCode);
            }
            jb.put("code", "0");
            jb.put("message", "?????????????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 9:28
     **/
    public String updateParkCode(String parkCode, String parkname, String berthcount, String address,
                                 String principal, String phone) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {

            // ---- ?????? ----
            // 1.??????cameraData
            cameraDataRepository.delCameraDataAll();
            // 2.??????parkChargeInfo
            parkChargeInfoRepository.delParkChargeInfoAll();
            // 3.??????parkDetectInfo
            parkDetectInfoRepository.delParkDetectInfoAll();
            // 4.??????parkInnerCars
            parkInnerCarsRepository.deleteParkInnerCarsAll();
            // 5.??????parkPayDetail
            parkPayDetailRepository.delParkPayDetailAll();

            // ---- ?????? ----
            parkRegionInfoRepository.updateParkRegionInfoAll(parkCode);
            gateInoRepository.updateGateInfoAll(parkCode);

            // ?????????????????????
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCodeTmp = properties.getProperty("park_code");
            ParkInfo park = parkInfoRepository.findParkInfoById(parkCodeTmp);
            ParkInfo parkTmp = new ParkInfo();

            BeanUtils.copyProperties(park, parkTmp, "parkcode");
            parkTmp.setParkcode(parkCode);
            parkTmp.setParkcode(parkCode);
            parkTmp.setParkname(parkname);
            parkTmp.setBerthCount(Integer.valueOf(berthcount));
            parkTmp.setAddress(address);
            parkTmp.setPrincipal(principal);
            parkTmp.setPhone(phone);
            parkTmp.setRemainBerthCount(Integer.valueOf(berthcount));
            parkTmp.setRectifyCount(0);
            parkTmp.setDownloadstatus(0);
            parkTmp.setFirstSecond(0);
            parkTmp.setKeepCloseOrCancle(0);
            parkTmp.setPort(0);
            parkTmp.setVoiceChannel(0);
            parkInfoRepository.save(parkTmp);
            parkInfoRepository.delete(park);

            // ??????sys_config
            sysConfigMapper.updateSysConfigByKey("park_code", parkCode);
            sysConfigMapper.updateSysConfigByKey("domain_id", "");
            sysConfigMapper.updateSysConfigByKey("domain_key", "");

            // ???????????? 
            sysConfigService.updateSysConfig();

            jb.put("code", "0");
            jb.put("message", "?????????????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 14:41
     **/
    public String updateParkInfo(String parkname, String address, String principal, String phone) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {

            // ?????????????????????
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCodeTmp = properties.getProperty("park_code");
            ParkInfo park = parkInfoRepository.findParkInfoById(parkCodeTmp);
            park.setParkname(parkname);
            park.setAddress(address);
            park.setPrincipal(principal);
            park.setPhone(phone);
            parkInfoRepository.save(park);

            // ????????????
            sysConfigService.updateSysConfig();

            jb.put("code", "0");
            jb.put("message", "???????????????????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 15:18
     **/
    public String getSysConfig() {
        JSONObject jb = new JSONObject();
        try {

            List<SysConfig> list = sysConfigRepository.findSysConfigAll();
            Map<String, String> map = list.stream().collect(Collectors.toMap(com.lann.openpark.system.dao.entiy.SysConfig::getConfig_key, com.lann.openpark.system.dao.entiy.SysConfig::getConfig_value));
            JSONObject data = JSONObject.fromObject(map);


            jb.put("code", "0");
            jb.put("message", "?????????????????????????????????");
            jb.put("data", data);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 15:20
     **/
    public String updateParkConfig(SysConfigValueBean sysconfigValue) {
        JSONObject jb = new JSONObject();
        try {

            Map<String, String> map = org.apache.commons.beanutils.BeanUtils.describe(sysconfigValue);
            for (String key : map.keySet()) {
                log.info("key: " + key);
                if (!key.equals("class")) {
                    sysConfigMapper.updateSysConfigByKey(key, map.get(key));
                }
            }

            // ????????????
            sysConfigService.updateSysConfig();

            jb.put("code", "0");
            jb.put("message", "?????????????????????????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/4 16:52
     **/
    public String querygates() {
        JSONObject jb = new JSONObject();
        try {
            // ??????gate??????
            List<RegionGateBean> gateList = gateInfoMapper.findGateInfoAll();
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(gateList));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:04
     **/
    public String queryrRegions() {
        JSONObject jb = new JSONObject();
        try {
            // ??????region??????
            List<ParkRegionInfo> regionList = parkRegionInfoRepository.findParkRegionInfoList();
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(regionList));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:37
     **/
    public String deleteRegion(String regionCode) {
        JSONObject jb = new JSONObject();
        try {
            ParkRegionInfo region = parkRegionInfoRepository.findRegionInfoByRegionCode(regionCode);
            if (null != region) {

                // ????????????????????????????????????
                List<GateInfo> list = gateInfoRepository.findGateInfoByRegionCode(regionCode);
                if (list.size() > 0) {
                    jb.put("code", "1");
                    jb.put("message", "??????????????????????????????????????????");
                    return jb.toString();
                }

                parkRegionInfoRepository.delete(region);
            }

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:03
     **/
    public String deleteGate(String pointcode) {
        JSONObject jb = new JSONObject();
        try {
            // ??????gate??????
            GateInfo gate = gateInoRepository.findGateInfoByPoineCode(pointcode);
            if (null != gate) {

                // ????????????????????????????????????
                List<EquipBean> list = equipInfoMapper.findEquipByPointCode(pointcode);
                if (list.size() > 0) {
                    jb.put("code", "1");
                    jb.put("message", "??????????????????????????????????????????");
                    return jb.toString();
                }

                gateInfoRepository.delete(gate);
            }

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:04
     **/
    public String queryEquipInfo() {
        JSONObject jb = new JSONObject();
        try {
            // ??????gate??????
            List<EquipGateBean> equipList = equipInfoMapper.findEquipGateList();
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(equipList));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:14
     **/
    public String deleteEquipInfo(String devicecode) {
        JSONObject jb = new JSONObject();
        try {
            // ??????gate??????
            equipInfoMapper.deleteEquipByDeviceCode(devicecode);
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 9:53
     **/
    public String addGateInfo(String pointcode, String pointname, String regioncode, String pointFunc) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // ?????????????????????
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String message = "????????????";
            if (StringUtils.isEmpty(pointcode)) {
                GateInfo gate = new GateInfo();
                gate.setPointname(pointname);
                gate.setParkcode(parkCode);

                if (StringUtils.isEmpty(pointFunc)) {
                    gate.setPointFunc("0");
                } else {
                    gate.setPointFunc(pointFunc);
                }

                gate.setPosition("0");
                gate.setRegionCode(regioncode);
                gateInfoRepository.save(gate);
            } else {// ??????
                message = "????????????";
                GateInfo gate = gateInoRepository.findGateInfoByPoineCode(pointcode);
                if (null != gate) {
                    gate.setPointname(pointname);
                    if (StringUtils.isEmpty(pointFunc)) {
                        gate.setPointFunc("0");
                    } else {
                        gate.setPointFunc(pointFunc);
                    }
                    gate.setRegionCode(regioncode);
                    gateInfoRepository.save(gate);
                }
            }

            jb.put("code", "0");
            jb.put("message", message);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/11 10:14
     **/
    public String addRegionInfo(String regionCode, String regionName, String berthCount, String regionType,
                                String parentRegion, String restrictedAccess, String whitelistPrivileges) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // ?????????????????????
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            if (StringUtils.isEmpty(regionCode)) {
                ParkRegionInfo parkRegionInfo = new ParkRegionInfo();
                parkRegionInfo.setRegionName(regionName);
                parkRegionInfo.setParkcode(parkCode);
                int berthCountNum = Integer.valueOf(berthCount.trim());
                parkRegionInfo.setBerthCount(berthCountNum);
                parkRegionInfo.setRegionRectifyCount(berthCountNum);
                parkRegionInfo.setJudgeRemainParkingNumber(0);
                // mod by sq 20210802
                parkRegionInfo.setRegionType(regionType);
                parkRegionInfo.setParentRegion(parentRegion);
                // ????????????????????????
                if (StringUtils.isNotEmpty(parentRegion)) {
                    // ??????????????????
                    ParkRegionInfo parentRegionInfo = parkRegionInfoRepository.findRegionInfoByRegionCode(parentRegion);
                    parkRegionInfo.setParentRegionName(parentRegionInfo.getRegionName());
                }
                // ????????????????????????
                parkRegionInfo.setRestrictedAccess(restrictedAccess);
                parkRegionInfo.setWhitelistPrivileges(whitelistPrivileges);

                parkRegionInfoRepository.save(parkRegionInfo);

                jb.put("code", "0");
                jb.put("message", "????????????");
                return jb.toString();
            } else {
                ParkRegionInfo region = parkRegionInfoRepository.findRegionInfoByRegionCode(regionCode);
                if (null != region) {
                    region.setRegionName(regionName);
                    region.setBerthCount(Integer.parseInt(berthCount.trim()));

                    // mod by sq 20210802
                    region.setRegionType(regionType);
                    region.setParentRegion(parentRegion);
                    // ????????????????????????
                    if (StringUtils.isNotEmpty(parentRegion)) {
                        // ??????????????????
                        ParkRegionInfo parentRegionInfo = parkRegionInfoRepository.findRegionInfoByRegionCode(parentRegion);
                        region.setParentRegionName(parentRegionInfo.getRegionName());
                    }
                    // ????????????????????????
                    region.setRestrictedAccess(restrictedAccess);
                    region.setWhitelistPrivileges(whitelistPrivileges);

                    parkRegionInfoRepository.save(region);
                }
                jb.put("code", "0");
                jb.put("message", "????????????");
                return jb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @return
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 10:51
     */
    public ResultVO addEquipInfo(EquipInfo equipInfo) {
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // ?????????????????????
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            // ??????equip??????
            EquipInfo equipInfoTmp = equipInfoRepository.findEquipInfoByDevicecode(equipInfo.getDevicecode());
            // ?????????????????????????????????
            if (null != equipInfoTmp) {
                equipInfoTmp.setDevicename(equipInfo.getDevicename());
                equipInfoTmp.setVideofunc(equipInfo.getVideofunc());
                equipInfoTmp.setPointcode(equipInfo.getPointcode());
                equipInfoTmp.setIp(equipInfo.getIp());
                equipInfoTmp.setVoiceChannel(equipInfo.getVoiceChannel());
                equipInfoRepository.save(equipInfoTmp);
            } else {
                // ?????????????????????
                equipInfo.setBindIp(equipInfo.getIp());
                equipInfo.setPort(80);
                equipInfo.setBuildtime(new Date());
                equipInfo.setRunMode("1");
                equipInfo.setTimeBegin(new Time(0));
                equipInfo.setTimeEnd(new Time(0));
                equipInfo.setDownloadstatus(1);
                equipInfo.setKeepCloseOrCancle(1);
                equipInfo.setFirstSecond(1);
                equipInfo.setVoiceChannel(equipInfo.getVoiceChannel());
                equipInfoRepository.save(equipInfo);
            }
            return ResultVOUtil.success();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            return ResultVOUtil.error(ResultEnum.EXCEPTION);
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiangd
     * @Description
     * @Date 2020/11/5 14:08
     **/
    public String queryLeaguerList(String plateNo, String phoneNo, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<ParkVipInfo> pages = parkVipInfoMapper.findParkVipInfo4Client(plateNo, phoneNo);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult(), jsonConfig));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/5/26 15:58
     **/
    public String queryParkLeaguerList(String plateNo, String phoneNo, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<ParkVipInfo> pages = parkVipInfoMapper.queryParkLeaguerList(plateNo, phoneNo);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult(), jsonConfig));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/16 9:30
     **/
    public List<ParkVipInfo> queryLeaguerAll() {
        try {
            List<ParkVipInfo> list = parkVipInfoRepository.queryLeaguerAll();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            List<ParkVipInfo> list = new ArrayList<ParkVipInfo>();
            return list;
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 15:30
     **/
    public String addLeaguerInfo(ParkVipInfoParams parkVipInfoParams) {
        JSONObject jb = new JSONObject();
        try {
            String carno = parkVipInfoParams.getCarno();
            String licenceType = parkVipInfoParams.getLicenceType();
            ParkVipInfo vip = parkVipInfoRepository.findGateInfoByCarno(carno, licenceType);
            if (null != vip) {// vip????????????
                jb.put("code", "1");
                jb.put("message", "???????????????????????????????????????");
                return jb.toString();
            } else {
                ParkVipInfo parkVipInfo = new ParkVipInfo();

                BeanUtils.copyProperties(parkVipInfoParams, parkVipInfo, "validBegintime", "expiryDate");
                Date start = DateUtil.parseDateYMDHMS(parkVipInfoParams.getValidBegintime());
                Date end = DateUtil.parseDateYMDHMS(parkVipInfoParams.getExpiryDate());
                parkVipInfo.setValidBegintime(start);
                parkVipInfo.setExpiryDate(end);
                parkVipInfo.setVipType("1");
                parkVipInfo.setDeleteStatus("1");
                parkVipInfo.setGroupNid("1");
                parkVipInfo.setCarnoType("02");
                parkVipInfoRepository.save(parkVipInfo);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    public String addLeaguerInfo1(ParkVipInfoParams parkVipInfoParams) {
        JSONObject jb = new JSONObject();
        try {

            ParkVipInfo parkVipInfo = new ParkVipInfo();
            BeanUtils.copyProperties(parkVipInfoParams, parkVipInfo, "validBegintime", "expiryDate");
            Date start = DateUtil.parseDateYMDHMS(parkVipInfoParams.getValidBegintime());
            Date end = DateUtil.parseDateYMDHMS(parkVipInfoParams.getExpiryDate());
            parkVipInfo.setValidBegintime(start);
            parkVipInfo.setExpiryDate(end);
            parkVipInfo.setVipType("1");
            parkVipInfo.setDeleteStatus("1");
            parkVipInfo.setGroupNid("1");
            parkVipInfo.setCarnoType("02");
            parkVipInfoRepository.save(parkVipInfo);

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    public String addLeaguerInfo4Client(ParkVipInfoParams parkVipInfoParams) {
        JSONObject jb = new JSONObject();
        try {

            ParkLeaguer parkLeaguer = new ParkLeaguer();
            parkLeaguer.setLeaguerName(parkVipInfoParams.getName());
            parkLeaguer.setLeaguerPhone(parkVipInfoParams.getPhone());
            Date start = DateUtil.parseDateYMDHMS(parkVipInfoParams.getValidBegintime());
            Date end = DateUtil.parseDateYMDHMS(parkVipInfoParams.getExpiryDate());
            parkLeaguer.setStartDate(start);
            parkLeaguer.setEndDate(end);
            parkLeaguerRepository.save(parkLeaguer);

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 16:28
     **/
    public String deleteLeaguer(String vipId) {
        JSONObject jb = new JSONObject();
        try {
            ParkVipInfo vip = parkVipInfoRepository.findGateInfoByVipId(vipId);
            if (null != vip) {
                parkVipInfoRepository.delete(vip);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/5 17:00
     **/
    public String xqLeaguer(String vipId, String validBegintime, String expiryDate) {
        JSONObject jb = new JSONObject();
        try {
            ParkVipInfo vip = parkVipInfoRepository.findGateInfoByVipId(vipId);
            if (null != vip) {
                Date start = DateUtil.parseDateYMDHMS(validBegintime);
                Date end = DateUtil.parseDateYMDHMS(expiryDate);

                vip.setValidBegintime(start);
                vip.setExpiryDate(end);

                parkVipInfoRepository.save(vip);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 9:22
     **/
    public String queryUserList() {
        JSONObject jb = new JSONObject();
        try {
            List<ParkUserInfo> list = parkUserInfoRepository.findParkUserInfoList();
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(list));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 9:58
     **/
    public String addUserInfo(ParkUserInfo userinfo) {
        JSONObject jb = new JSONObject();
        try {
            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userinfo.getUserid());
            if (null != user) {// ?????????????????????
                jb.put("code", "1");
                jb.put("message", "?????????????????????");
                return jb.toString();
            } else {
                parkUserInfoRepository.save(userinfo);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 10:07
     **/
    public String deleteUserInfo(String userid) {
        JSONObject jb = new JSONObject();
        try {
            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userid);
            if (null != user) {
                parkUserInfoRepository.delete(user);
                // ?????????????????????
                userRoleRepository.deleteRoleByUserId(userid);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 10:49
     **/
    public String changeUserPassword(String userid, String password) {
        JSONObject jb = new JSONObject();
        try {
            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userid);
            if (null != user) {
                user.setPassword(password);
                parkUserInfoRepository.save(user);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 11:27
     **/
    public String queryRuleList() {
        JSONObject jb = new JSONObject();
        try {
            List<Charge2Policy> list = charge2PolicyRepository.findChar2PolicyList();
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(list));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }


    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/11 14:02
     **/
    public String saveChargePolicy(String policyJson) {
        JSONObject jb = new JSONObject();
        try {
            JSONObject pjson = JSONObject.fromObject(policyJson);
            String nid = pjson.getString("nid");
            Charge2Policy charge2Policy = null;
            if (StringUtils.isEmpty(nid)) {
                charge2Policy = new Charge2Policy();
            } else {
                charge2Policy = charge2PolicyRepository.findChar2PolicyByNid(nid);
                if (null == charge2Policy) {
                    charge2Policy = new Charge2Policy();
                }
            }
            String policyName = pjson.getString("policyName");
            String referCost = pjson.getString("referCost");
            String plateType = pjson.getString("plateType");
            String description = pjson.getString("description");
            JSONObject configTemplate = pjson.getJSONObject("configTemplate");
            charge2Policy.setPolicyName(policyName);
            charge2Policy.setReferCost(referCost);
            charge2Policy.setPlateType(plateType);
            charge2Policy.setDescription(description);
            charge2Policy.setConfigTemplate(configTemplate.toString());
            charge2PolicyRepository.save(charge2Policy);

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 9:27
     **/
    public String deleteChargePolicy(String nid) {
        JSONObject jb = new JSONObject();
        try {
            // ????????????nid??????????????????
            Charge2Policy policy = charge2PolicyRepository.findChar2PolicyByNid(nid);
            if (null != policy) {
                // ?????????????????????????????????????????????????????????
                List<ChargePlan> list = chargePlanRepository.findChargePlanByPolicyId(nid);
                if (list.size() > 0) {
                    jb.put("code", "1");
                    jb.put("message", "????????????????????????????????????????????????????????????");
                    return jb.toString();
                }
                charge2PolicyRepository.delete(policy);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/6 11:49
     **/
    public String querySchmeneList() {
        JSONObject jb = new JSONObject();
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        try {
            List<ChargePlan> list = chargePlanRepository.findChargePlanList();
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(list, jsonConfig));
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 11:41
     **/
    public String saveChargePlan(ChargePlanParams chargePlanParams) {
        JSONObject jb = new JSONObject();
        try {

            ChargePlan chargePlan = new ChargePlan();
            BeanUtils.copyProperties(chargePlanParams, chargePlan, "beginDate", "endDate");
            chargePlan.setBeginDate(DateUtil.parseDateYMDHMS(chargePlanParams.getBeginDate()));
            chargePlan.setEndDate(DateUtil.parseDateYMDHMS(chargePlanParams.getEndDate()));
            if (StringUtils.isNotEmpty(chargePlan.getNid())) {// ??????
                // ?????????????????????
                chargePlanConfigRepository.deletePlanConfigByPlanId(chargePlan.getNid());
                chargePlanRegionRepository.deletePlanRegionByPlanId(chargePlan.getNid());
            }
            // ????????????????????????
            String[] carTypeArr = chargePlan.getCarTypes().split(";");
            for (int i = 0; i < carTypeArr.length; i++) {
                ChargePlanConfig bean = new ChargePlanConfig();
                bean.setParkId("");// ??????????????????????????????????????? mod by sq 20200703
                bean.setPlanId(chargePlan.getNid());
                bean.setPlateType(String.valueOf(carTypeArr[i]));// ????????????
                bean.setPolicyId(chargePlan.getChargePolicyId());// ??????????????????
                bean.setIsEffective("0101");
                bean.setPriority((int) chargePlan.getPolicyPriority());// ?????????
                bean.setUpdateTime(new Date());
                chargePlanConfigRepository.save(bean);
            }
            // ?????????????????????????????????
            String[] regionArr = chargePlanParams.getRegions().split(";");
            for (int i = 0; i < regionArr.length; i++) {
                ChargePlanRegion planRegion = new ChargePlanRegion();
                planRegion.setPlanId(chargePlan.getNid());
                planRegion.setRegionCode(regionArr[i]);
                chargePlanRegionRepository.save(planRegion);
            }
            chargePlanRepository.save(chargePlan);
            // ????????????
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 15:42
     **/
    public String deleteChargePlan(String nid) {
        JSONObject jb = new JSONObject();
        try {
            chargePlanConfigRepository.deletePlanConfigByPlanId(nid);
            ChargePlan chargePlan = chargePlanRepository.findChargePlanByNid(nid);
            if (chargePlan != null) {
                chargePlanRepository.delete(chargePlan);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/12 16:20
     **/
    public String calcParkfee(String timeIn, String timeOut, String policyId) {
        JSONObject jb = new JSONObject();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeInStr = DateUtil.formatDateYMDHMS(DateUtil.parseDateYMDHMS(timeIn));
            String timeOutStr = DateUtil.formatDateYMDHMS(DateUtil.parseDateYMDHMS(timeOut));
            DateTime timeInJoda = new DateTime(df.parse(timeInStr));
            DateTime timeOutJoda = new DateTime(df.parse(timeOutStr));
            // ??????????????????
            ParkingInfo parkingInfo = new ParkingInfo(timeInJoda.toDate(), timeOutJoda.toDate());
            // ??????????????????
            Charge2Policy charge2Policy = charge2PolicyRepository.findChar2PolicyByNid(policyId);
            if (null == charge2Policy) {// ???????????????????????????
                jb.put("code", "1");
                jb.put("message", "????????????????????????");
                return jb.toString();
            }
            String template = charge2Policy.getConfigTemplate();// ??????????????????json???
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(template);
            // ??????????????????
            Rule rule = new Rule();
            List<LocalTime[]> freeTimeSections = getFreeTimeSectionsByJson(json.getJSONArray("freeTimeSections"));
            rule.setFreeTimeSections(freeTimeSections);
            FreeTimeConfig freeTimeConfig = getFreeTimeConfigByJson(json.getJSONObject("freeTimeConfig"));
            rule.setFreeTimeConfig(freeTimeConfig);
            rule = getCaclRule(rule, json.getJSONObject("dayNightConfig"));
            BorrowTimeConfig borrowTimeConfig = getBorrowTimeConfigByJson(json.getJSONObject("borrowTimeConfig"));
            rule.setBorrowTimeConfig(borrowTimeConfig);
            LoopChargeConfig loopConfig = getLoopChargeConfigByJson(json.getJSONObject("loopConfig"));
            rule.setLoopConfig(loopConfig);
            rule.setLimitCharge(json.getDoubleValue("limitCharge"));
            AttachRule attachRule = getAttachRuleByJson(json.getJSONObject("attachRule"));
            rule.setAttachRule(attachRule);

            // ??????????????????
            ParkingAlgorithm algorithm = new ParkingAlgorithm(parkingInfo, rule);
            // ??????
            algorithm.executeCalculate();

            // ??????????????????????????????????????????
//            int days = Days.daysBetween(parkingInfo.getCalcDriveIn(), parkingInfo.getCaclDriveOut()).getDays();
//            double limit = rule.getLimitCharge();
//            if (days == 0 && limit < algorithm.getParkingInfo().getCost()) {
//                log.info("????????????????????????" + algorithm.getParkingInfo().getCost());
//                algorithm.getParkingInfo().setCost(limit);
//            }

            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("parkLength", algorithm.getParkingInfo().getParkLength());
            jb.put("cost", algorithm.getParkingInfo().getCost());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    private List<LocalTime[]> getFreeTimeSectionsByJson(com.alibaba.fastjson.JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<LocalTime[]> freeTimeSections = (List) new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            com.alibaba.fastjson.JSONObject json = jsonArray.getJSONObject(i);
            int beginMinute = json.getIntValue("beginMinute");
            int endMinute = json.getIntValue("endMinute");
            LocalTime localTime = new LocalTime();
            LocalTime beginLocalTime = localTime.withMillisOfDay(beginMinute * 60 * 1000);
            LocalTime endMinuteLocalTime = localTime.withMillisOfDay(endMinute * 60 * 1000);
            LocalTime[] section = {beginLocalTime, endMinuteLocalTime};
            freeTimeSections.add(section);
        }
        if (freeTimeSections.size() > 0) {
            return freeTimeSections;
        }
        return null;
    }

    private FreeTimeConfig getFreeTimeConfigByJson(com.alibaba.fastjson.JSONObject json) {
        FreeTimeConfig config = new FreeTimeConfig();
        if (json == null) {
            return config;
        }
        String freeMode = json.getString("freeMode");
        if ("4001".equals(freeMode)) {
            config.setFreeMode("A0105");
        } else {
            config.setFreeMode("A0106");
        }
        config.setFreeTime(json.getIntValue("freeTime"));
        config.setFreeLimitTime(json.getIntValue("freeLimitTime"));

        String allDayAvailable = json.getString("allDayAvailable");

        if (allDayAvailable != null) {
            config.setAllDayAvailable(Boolean.valueOf(allDayAvailable).booleanValue());
        } else {
            String dayAvailable = json.getString("dayAvailable");
            String nightAvailable = json.getString("nightAvailable");
            if (dayAvailable != null) {
                config.setDayAvailable(Boolean.valueOf(dayAvailable).booleanValue());
            }
            if (nightAvailable != null) {
                config.setNightAvailable(Boolean.valueOf(nightAvailable).booleanValue());
            }
        }
        return config;
    }

    private Rule getCaclRule(Rule rule, com.alibaba.fastjson.JSONObject json) {
        if (json == null) {
            System.out.println("???????????????????????????Exception,???????????????????????????");
            return rule;
        }
        String caclType = json.getString("type");
        try {
            if ("6501".equals(caclType)) {
                rule.setCalcType("A0109");
            } else {
                com.alibaba.fastjson.JSONObject o = json.getJSONObject("dayInterval");
                rule.setCalcType("A0110");
                rule.setDayInterval(new int[]{o.getIntValue("beginMinute"), o.getIntValue("endMinute")});
            }
        } catch (Exception e) {
            this.log.error("????????????", e);
            return rule;
        }

        ChargeRuleUnit allDayChargeConfig = getChargeRuleUnitByJson(json.getJSONObject("allDayChargeConfig"));
        ChargeRuleUnit dayChargeConfig = getChargeRuleUnitByJson(json.getJSONObject("dayChargeConfig"));
        ChargeRuleUnit nightChargeConfig = getChargeRuleUnitByJson(json.getJSONObject("nightChargeConfig"));

        rule.setAllDayChargeConfig(allDayChargeConfig);
        rule.setDayChargeConfig(dayChargeConfig);
        rule.setNightChargeConfig(nightChargeConfig);
        return rule;
    }

    private ChargeRuleUnit getChargeRuleUnitByJson(com.alibaba.fastjson.JSONObject json) {
        if (json == null) {
            return null;
        }
        ChargeRuleUnit chargeRuleUnit = new ChargeRuleUnit();
        String type = json.getString("type");
        if (type.equals("6301")) {
            PeriodChargeRule periodRule = new PeriodChargeRule();
            com.alibaba.fastjson.JSONArray array = json.getJSONArray("periodRule");
            for (int i = 0; i < array.size(); i++) {
                com.alibaba.fastjson.JSONObject o = array.getJSONObject(i);
                int beginMinute = o.getIntValue("beginMinute");
                int endMinute = o.getIntValue("endMinute");
                int timeUnit = o.getIntValue("unitMinute");
                Double unitCost = Double.valueOf(o.getDoubleValue("unitPrice"));
                periodRule.addPeriodUnit(beginMinute, endMinute, timeUnit, unitCost.doubleValue());
            }
            chargeRuleUnit.setPeriodRule(periodRule);
        } else {
            DurationChargeRule duractionRule = new DurationChargeRule();
            com.alibaba.fastjson.JSONArray array = json.getJSONArray("duractionRule");
            for (int i = 0; i < array.size(); i++) {
                com.alibaba.fastjson.JSONObject o = array.getJSONObject(i);
                int beginMinute = o.getIntValue("beginMinute");
                int endMinute = o.getIntValue("endMinute");
                int timeUnit = o.getIntValue("unitMinute");
                Double unitCost = Double.valueOf(o.getDoubleValue("unitPrice"));
                duractionRule.addDuractionUnit(beginMinute, endMinute, timeUnit, unitCost.doubleValue());
            }
            chargeRuleUnit.setDuractionRule(duractionRule);
        }
        return chargeRuleUnit;
    }


    private AttachRule getAttachRuleByJson(com.alibaba.fastjson.JSONObject json) {
        AttachRule attachRule = new AttachRule();
        if (json == null) {
            return attachRule;
        }

        attachRule.setAttachMoney(json.getDoubleValue("attachMoney"));
        com.alibaba.fastjson.JSONObject attachPeriod = json.getJSONObject("attachPeriod");
        if (attachPeriod != null) {
            attachRule.setValidParkMinutes(attachPeriod.getIntValue("validParkMinutes"));
            com.alibaba.fastjson.JSONArray array = attachPeriod.getJSONArray("periods");
            for (int i = 0; i < array.size(); i++) {
                com.alibaba.fastjson.JSONObject o = array.getJSONObject(i);
                int beginMinute = o.getIntValue("beginMinute");
                int endMinute = o.getIntValue("endMinute");
                int timeUnit = o.getIntValue("unitMinute");
                Double unitCost = Double.valueOf(o.getDoubleValue("unitPrice"));
                attachRule.addPeriodUnit(beginMinute, endMinute, timeUnit, unitCost.doubleValue());
            }
        }
        return attachRule;
    }


    private LoopChargeConfig getLoopChargeConfigByJson(com.alibaba.fastjson.JSONObject json) {
        LoopChargeConfig loopConfig = new LoopChargeConfig();
        if (json == null) {
            return loopConfig;
        }
        String loopType = json.getString("loopType");
        if ("6101".equals(loopType)) {
            loopConfig.setLoopType("A0108");
        } else {
            loopConfig.setLoopType("A0107");
        }
        loopConfig.setCustomPoint(json.getIntValue("customPoint"));
        return loopConfig;
    }

    private BorrowTimeConfig getBorrowTimeConfigByJson(com.alibaba.fastjson.JSONObject json) {
        BorrowTimeConfig borrowTimeConfig = new BorrowTimeConfig();
        if (json == null) {
            return borrowTimeConfig;
        }
        borrowTimeConfig.setAllDayTimeCanBeTaken(json.getBooleanValue("allDayTimeCanBeTaken"));
        borrowTimeConfig.setDayTimeCanBeTaken(json.getBooleanValue("dayTimeCanBeTaken"));
        borrowTimeConfig.setNightTimeCanBeTaken(json.getBooleanValue("nightTimeCanBeTaken"));
        borrowTimeConfig.setTimeLimit(json.getIntValue("timeLimit"));
        return borrowTimeConfig;
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/11/17 8:31
     **/
    public String getCurrentOrder(String deviceNo) {
        JSONObject jb = new JSONObject();
        try {

            // ????????????????????????????????????????????????
            CameraData cameraData = cameraDataRepository.findLastCameraDataByDeviceNo(deviceNo);

            if (null == cameraData) {
                JSONObject ret = new JSONObject();
                ret.put("result", "1");
                ret.put("msg", "????????????????????????");
                return ret.toString();
            }

            if (null == cameraData.getInsertTime()) {
                JSONObject ret = new JSONObject();
                ret.put("result", "2");
                ret.put("msg", "??????????????????");
                return ret.toString();
            }

            String plateNo = cameraData.getLicense();
            int plateType = cameraData.getPlateType();// ????????????
            // ???????????????????????????????????????????????????
            List<ParkChargeInfo> list_parkChargeInfo = orderservice.findOutCarOrder(plateNo, String.valueOf(plateType));
            if (list_parkChargeInfo.size() <= 0) {
                JSONObject ret = new JSONObject();
                ret.put("result", "3");
                ret.put("msg", "????????????????????????");
                return ret.toString();
            }
            ParkChargeInfo parkChargeInfo = list_parkChargeInfo.get(0);
            if (!parkChargeInfo.getExitType().equals("7")) {
                JSONObject ret = new JSONObject();
                ret.put("result", "4");
                ret.put("msg", "??????????????????");
                return ret.toString();
            }

            if (parkChargeInfo.getTotalcharge() <= 0) {
                JSONObject ret = new JSONObject();
                ret.put("result", "7");
                ret.put("msg", "0???????????????????????????");
                return ret.toString();
            }

            jb.put("orderNo", parkChargeInfo.getNid());// ????????????
            jb.put("plateNo", parkChargeInfo.getCarno());// ????????????
            if (null != parkChargeInfo.getCollectiondate1()) {
                String timeIn = DateUtil.formatDateYMDHMS(parkChargeInfo.getCollectiondate1());
                jb.put("timeIn", timeIn);// ????????????
            } else {
                JSONObject ret = new JSONObject();
                ret.put("result", "5");
                ret.put("msg", "???????????????");
                return ret.toString();
            }

            if (null != parkChargeInfo.getCollectiondate2()) {
                String timeOut = DateUtil.formatDateYMDHMS(parkChargeInfo.getCollectiondate2());
                jb.put("timeOut", timeOut);// ????????????
            } else {
                JSONObject ret = new JSONObject();
                ret.put("result", "6");
                ret.put("msg", "???????????????");
                return ret.toString();
            }


            jb.put("deviceCode", parkChargeInfo.getDevicecodeExt());// ????????????
            jb.put("payFee", parkChargeInfo.getTotalcharge());// ????????????
            jb.put("duration", parkChargeInfo.getParkDuration());
            jb.put("payType", "????????????");// ????????????
            jb.put("plateType", Constant.getCarTypeName(parkChargeInfo.getLicensetype()));

            jb.put("code", "0");
            jb.put("message", "????????????");

            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/16 15:56
     **/
    public String saveImportVipInfo(String jsonData) {
        JSONObject jb = new JSONObject();
        try {
            JSONArray ja = JSONArray.fromObject(jsonData);
            for (int i = 0; i < ja.size(); i++) {
                JSONObject jbVip = ja.getJSONObject(i);
                // ?????????????????????????????????
                String carno = jbVip.getString("carno");
                String licenceType = jbVip.getString("licenceType");
                ParkVipInfo parkVipInfo = parkVipInfoRepository.findGateInfoByCarno(carno, licenceType);
                if (null == parkVipInfo) {
                    parkVipInfo = new ParkVipInfo();
                }
                parkVipInfo.setCarno(carno);
                parkVipInfo.setLicenceType(licenceType);
                parkVipInfo.setPhone(jbVip.getString("phone"));
                parkVipInfo.setName(jbVip.getString("name"));
                parkVipInfo.setValidBegintime(DateUtil.parseDateYMDHMS(jbVip.getString("validBegintime")));
                parkVipInfo.setExpiryDate(DateUtil.parseDateYMDHMS(jbVip.getString("expiryDate")));
                parkVipInfo.setVipType("1");
                parkVipInfo.setDeleteStatus("1");
                parkVipInfo.setGroupNid("1");
                parkVipInfo.setCarnoType("02");
                parkVipInfoRepository.save(parkVipInfo);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 10:58
     **/
    public String deleteInnerCar(String orderNo, String userid) {
        JSONObject jb = new JSONObject();
        try {
            jb = this.closeOrder(orderNo, userid);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 11:26
     **/
    public String clearInnerCar(String userid, String regionCode, String start, String end) {
        JSONObject jb = new JSONObject();
        try {

            // ??????????????????
            Date startDate = null;
            Date endDate = null;
            try {
                if (StringUtils.isNotEmpty(start)) {
                    startDate = DateUtil.parseDateYMDHMS(start);
                }
            } catch (Exception e) {
                startDate = null;
            }
            try {
                if (StringUtils.isNotEmpty(end)) {
                    endDate = DateUtil.parseDateYMDHMS(end);
                }
            } catch (Exception e) {
                endDate = null;
            }

            // ????????????????????????
            List<ParkChargeInfo> list = null;
            if (StringUtils.isNotEmpty(regionCode)) {
                list = parkChargeInfoRepository.findAllUnclosedOrderByRegionCode(regionCode, startDate, endDate);
            } else {
                list = parkChargeInfoRepository.findAllUnclosedOrder(startDate, endDate);
            }
            if (list.size() > 0) {
                for (ParkChargeInfo order : list) {
                    this.closeOrder(order.getNid(), userid);
                }
            }
            jb.put("code", "0");
            jb.put("message", "??????????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 11:30
     **/
    private JSONObject closeOrder(String orderNo, String userid) {
        JSONObject jb = new JSONObject();
        try {

            // ??????????????????
            ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
            if (null == parkChargeInfo) {
                jb.put("code", "0");
                jb.put("message", "????????????");
                return jb;
            }
            // ??????????????????
            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userid);
            if (null == user) {
                jb.put("code", "1");
                jb.put("message", "??????????????????????????????");
                return jb;
            }
            // ????????????
            parkChargeInfo.setCollectiondate2(parkChargeInfo.getCollectiondate1());
            parkChargeInfo.setParkDuration(0);
            parkChargeInfo.setTotalcharge(0f);
            if (parkChargeInfo.getCharge() <= 0) {
                parkChargeInfo.setCharge(0);
                parkChargeInfo.setRealcharge(0f);
                parkChargeInfo.setDerateFee(0);
                parkChargeInfo.setCouponFee(0);
                parkChargeInfo.setDiscount(0);
            }
            parkChargeInfo.setOperator("0");
            parkChargeInfo.setExitType(Constant.EXIT_TYPE_OPEN_DEL);// ????????????
            parkChargeInfo.setOutUpload(0);// ??????????????????
            parkChargeInfo.setPayType(Constant.PAY_TYPE_CASH);
            parkChargeInfo.setPayTime(new Date());
            parkChargeInfoRepository.save(parkChargeInfo);
            // ??????????????????
            Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, null, "8102"));
            upLoadDriveOutThread.start();
//            // ??????????????????
//            Thread upLoadPayDetailThread = new Thread((Runnable) new UpLoadPayDetailThread(parkChargeInfo, null, null));
//            upLoadPayDetailThread.start();
            // ??????????????????
            // ????????????????????????????????????
            EquipInfo equip = equipInfoRepository.findEquipInfoByDevicecode(parkChargeInfo.getDevicecode());
            GateInfo gate = gateInfoRepository.findGateInfoByPoineCode(equip.getPointcode());
            if (null != gate) {
                parkService.updateBerthCount(1, gate.getRegionCode());
            }
            // ????????????
            LogDelInnerCars logDelInnerCars = new LogDelInnerCars();
            logDelInnerCars.setCarno(parkChargeInfo.getCarno());
            logDelInnerCars.setOrderNo(parkChargeInfo.getNid());
            logDelInnerCars.setRegionCode(gate.getRegionCode());
            logDelInnerCars.setUserId(user.getUserid());
            logDelInnerCars.setUserName(user.getUsername());
            logDelInnerCars.setLogTime(new Date());
            logDelInnerCarsRepository.save(logDelInnerCars);

            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "?????????????????????");
            jb.put("data", new JSONArray());
            return jb;
        }


    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 11:52
     **/
    public String queryDelOrderList(String startDate, String endDate, String plateNo, String userid, String regionCode, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<DelCars> pages = parkChargeInfoMapper.queryDelOrderList(startDate, endDate, plateNo, userid, regionCode);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 15:22
     **/
    public String queryPoleList(String startDate, String endDate, String userid, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<PoleControl> pages = parkChargeInfoMapper.queryPoleList(startDate, endDate, userid);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/21 10:45
     **/
    public String queryCarnoList(String startDate, String endDate, String userid, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<CarnoLogBean> pages = parkChargeInfoMapper.queryCarnoList(startDate, endDate, userid);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2020/12/17 16:13
     **/
    public String queryFreeGoList(String startDate, String endDate, String plateNo, String userid, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<FreeGos> pages = parkChargeInfoMapper.queryFreeGoList(startDate, endDate, plateNo, userid);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult()));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/13 14:17
     **/
    public String parkFeeDiscount(String orderNo, String day, String flag) {
        JSONObject jb = new JSONObject();
        try {
            // ??????????????????
            ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
            if ("first".equals(day)) {
                parkChargeInfo.setIsFristDayFree(flag);
            } else {
                parkChargeInfo.setIsLastDayFree(flag);
            }
            parkChargeInfoRepository.save(parkChargeInfo);
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/1/29 9:23
     **/
    public String parkStatistics(String start, String end) {
        JSONObject jb = new JSONObject();
        try {
            int je0 = 0;
            int je5 = 0;
            int je10 = 0;
            int je20 = 0;
            int je50 = 0;
            int je100 = 0;
            // ??????????????????
            List<ParkChargeInfo> listOrder = parkChargeInfoRepository.findOrderByTime(DateUtil.parseDateYMDHMS(start), DateUtil.parseDateYMDHMS(end));
            for (ParkChargeInfo parkChargeInfo : listOrder) {
                double je = parkChargeInfo.getCharge();// ????????????
                if (je == 0) {
                    je0++;
                } else if (je > 0 && je <= 5) {
                    je5++;
                } else if (je > 5 && je <= 10) {
                    je10++;
                } else if (je > 10 && je <= 20) {
                    je20++;
                } else if (je > 20 && je <= 50) {
                    je50++;
                } else if (je > 50) {
                    je100++;
                }
            }
            JSONArray jaOrder = new JSONArray();
            jaOrder.add(je0);
            jaOrder.add(je5);
            jaOrder.add(je10);
            jaOrder.add(je20);
            jaOrder.add(je50);
            jaOrder.add(je100);
            JSONObject jbData = new JSONObject();
            jbData.put("ddtj", jaOrder);

            // ???????????????????????????
            List<ParkIncomeBean> listIncome = parkChargeInfoMapper.incomeStatistics(start, end);
            JSONArray jaIncomeCate = new JSONArray();
            JSONArray jaIncomeData = new JSONArray();
            for (ParkIncomeBean bean : listIncome) {
                jaIncomeCate.add(bean.getCateName());
                JSONObject jb1 = new JSONObject();
                jb1.put("value", bean.getFee());
                jb1.put("name", bean.getCateName());
                jaIncomeData.add(jb1);
            }

            jbData.put("incomeCate", jaIncomeCate);
            jbData.put("incomeData", jaIncomeData);

            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", jbData);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/24 15:01
     **/
    public String manaulDriveOut(String plateNo, String licenceType, String deviceCode, String userId) {
        String userName = "";
        ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userId);
        if (null != user) {
            userName = user.getUsername();
        }
        JSONObject jb = new JSONObject();
        // ??????
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        try {
            // ???????????????????????????????????????
            // ????????????????????????????????????????????????????????????????????????????????????
            List<ParkChargeInfo> list_parkChargeInfo = orderservice.findOutCarOrder(plateNo, String.valueOf(licenceType));
            if (list_parkChargeInfo.size() <= 0) {
                jb.put("code", "4");
                jb.put("message", "????????????????????????????????????");
                return jb.toString();
            }
            String orderNo = list_parkChargeInfo.get(0).getNid();

            // ?????????????????????????????????
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(deviceCode);
            ehcacheUtil.put(Constant.PARK_CACHE, "manual_trigger_" + deviceCode, gateMsg);
            CameraData cameraData = null;
            Thread.sleep(1000);
            for (int i = 0; i < 10; i++) {
                log.info("??? " + (i + 1) + " ???????????????...");
                // ??????????????????cameraData???ID???
                // camId = (String) ehcacheUtil.get(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                cameraData = (CameraData) ehcacheUtil.get(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                if (cameraData != null) {
                    // ????????????
                    log.info("???????????????ID: " + cameraData.getId());
                    ehcacheUtil.remove(Constant.PARK_CACHE, "trigger_result_" + deviceCode);
                    break;
                } else {
                    Thread.sleep(500);
                }
            }
            // ???????????????????????????cameraData
            if (null == cameraData) {
                jb.put("code", "1");
                jb.put("message", "????????????");
                return jb.toString();
            }
//            // ????????????????????????
//            CameraData cameraData = cameraDataRepository.findCameraDataByNid(camId);
//            if (null == cameraData) {
//                jb.put("code", "2");
//                jb.put("message", "????????????????????????!");
//                return jb.toString();
//            }
            // ??????cameradata
            cameraData.setLicense(plateNo);
            cameraData.setPlateType(Integer.valueOf(licenceType));
            cameraDataRepository.save(cameraData);
            // ??????????????????
            List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(deviceCode);
            if (list_equip.size() <= 0) {
                jb.put("code", "3");
                jb.put("message", "drive out query equipInfo error!");
                return jb.toString();
            }
            EquipGateInfo equipGateInfo = list_equip.get(0);
            // ????????????!!!!!!!
            String retJsonStr = cameraService.parkDriveOut(cameraData, equipGateInfo);
            // ???????????????????????????log
            if (retJsonStr.indexOf("success=true") > -1) {
                // ??????????????????
                ParkChargeInfo order = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
                CarnoLogInfo ci = new CarnoLogInfo();
                ci.setCarno(order.getCarno());
                ci.setDeviceCode(equipGateInfo.getDevicecode());
                ci.setDeviceName(equipGateInfo.getDevicename());
                ci.setOptTime(new Date());
                ci.setUserId(userId);
                ci.setUserName(userName);
                ci.setImgPath(order.getCpicExitPath());
                carnoLogInfoRepository.save(ci);
            }

            jb.put("code", "0");
            jb.put("message", "????????????");
            // jb.put("data", jbData);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 9:39
     **/
    public String queryBlackList(String plateNo, String phoneNo, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<ParkVipInfo> pages = blackInfoMapper.findParkBlackInfo4Client(plateNo, phoneNo);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult(), jsonConfig));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 10:46
     **/
    public String addBlackInfo(ParkVipInfoParams parkVipInfoParams) {
        JSONObject jb = new JSONObject();
        try {
            String carno = parkVipInfoParams.getCarno();
            String licenceType = parkVipInfoParams.getLicenceType();
            ParkVipInfo black = blackInfoRepository.findBlackInfoByCarno(carno, licenceType);
            if (null != black) {// ?????????????????????
                jb.put("code", "1");
                jb.put("message", "???????????????????????????????????????");
                return jb.toString();
            } else {
                BlackInfo balckInfo = new BlackInfo();
                balckInfo.setCarno(parkVipInfoParams.getCarno());
                balckInfo.setCarnoType(parkVipInfoParams.getLicenceType());
                Date start = DateUtil.parseDateYMDHMS(parkVipInfoParams.getValidBegintime());
                Date end = DateUtil.parseDateYMDHMS(parkVipInfoParams.getExpiryDate());
                balckInfo.setValidBegintime(start);
                balckInfo.setExpiryDate(end);
                blackInfoRepository.save(balckInfo);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 11:08
     **/
    public String xqBlack(String blackId, String validBegintime, String expiryDate) {
        JSONObject jb = new JSONObject();
        try {
            BlackInfo black = blackInfoRepository.findBlackInfoByVipId(blackId);
            if (null != black) {
                Date start = DateUtil.parseDateYMDHMS(validBegintime);
                Date end = DateUtil.parseDateYMDHMS(expiryDate);

                black.setValidBegintime(start);
                black.setExpiryDate(end);

                blackInfoRepository.save(black);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ???????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 11:18
     **/
    public String deleteBlack(String balckId) {
        JSONObject jb = new JSONObject();
        try {
            BlackInfo black = blackInfoRepository.findBlackInfoByVipId(balckId);
            if (null != black) {
                blackInfoRepository.delete(black);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/3/31 17:03
     **/
    public String queryOrderPay(String nid) {
        JSONObject jb = new JSONObject();
        try {
            List<OrderPayInfo> list = parkChargeInfoMapper.queryOrderPay(nid);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", list);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 14:59
     **/
    public String queryAbnormalkList(String plateNo, String num, int pagenum, int pagesize) {
        JSONObject jb = new JSONObject();
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        try {
            PageHelper.startPage(pagenum, pagesize);
            Page<AbnormalOut> pages = blackInfoMapper.queryAbnormalkList(plateNo, num);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", JSONArray.fromObject(pages.getResult(), jsonConfig));
            jb.put("total", pages.getTotal());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 11:38
     **/
    public String deleteAbnormal(String id) {
        JSONObject jb = new JSONObject();
        try {
            AbnormalOut bean = abnormalOutRepository.findByAbnormalId(id);
            if (null != bean) {
                abnormalOutRepository.delete(bean);
            }
            // ??????????????????
            abnormalOutDetailRepository.deleByAid(id);
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 14:31
     **/
    public String clearAbnormal(int num) {
        JSONObject jb = new JSONObject();
        try {
            List<AbnormalOut> list = abnormalOutRepository.findListByCumulNum(num);
            if (list.size() > 0) {
                for (AbnormalOut bean : list) {
                    // ??????????????????????????????
                    abnormalOutRepository.delete(bean);
                    // ????????????????????????????????????
                    abnormalOutDetailRepository.deleByAid(bean.getId());
                }
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ??????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/1 15:00
     **/
    public String queryDetailList(String aid) {
        JSONObject jb = new JSONObject();
        try {
            // List<AbnormalOutDetail> list = abnormalOutDetailRepository.findDetailByAid(aid);
            List<AbnormalOutDetail> list = blackInfoMapper.queryAbnormalDetailList(aid);
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", list);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 10:53
     **/
    public String getMenu(String userId) {
        JSONObject jb = new JSONObject();
        try {
            JSONArray jaMenu = new JSONArray();
            if ("admin".equals(userId)) {// admin??????
                // ??????????????????
                List<ClientMenu> listOne = clientMenuRepository.findFirstLevelMenu("0");
                for (ClientMenu bean : listOne) {
                    if (bean.getMYxbz().equals("1")) {// ???????????????1
                        JSONObject jbMenuOne = new JSONObject();
                        jbMenuOne.put("name", bean.getMName());
                        jbMenuOne.put("index", bean.getMIndex());
                        jbMenuOne.put("mcss", bean.getMClass());
                        jbMenuOne.put("type", bean.getMType());
                        if ("subMenu".equals(bean.getMType())) {// ???????????????
                            JSONArray jaMenu1 = new JSONArray();
                            JSONObject jbMenuTwo = new JSONObject();
                            // ?????????????????????????????????????????????json??????
                            List<ClientMenu> listTwo = clientMenuRepository.findFirstLevelMenu(bean.getId());
                            for (ClientMenu bean1 : listTwo) {
                                if (bean1.getMYxbz().equals("1")) {// ????????????
                                    jbMenuTwo.put("name", bean1.getMName());
                                    jbMenuTwo.put("index", bean1.getMIndex());
                                    jbMenuTwo.put("type", bean1.getMType());
                                    jbMenuTwo.put("mcss", bean1.getMClass());
                                    jaMenu1.add(jbMenuTwo);
                                }
                            }
                            jbMenuOne.put("children", jaMenu1);
                        }
                        jaMenu.add(jbMenuOne);
                    }
                }
            } else {// ???admin??????
                List<ClientMenu> listOne = parkUserInfoMapper.findMenuList(userId, "0");
                for (ClientMenu bean : listOne) {
                    JSONObject jbMenuOne = new JSONObject();
                    jbMenuOne.put("name", bean.getMName());
                    jbMenuOne.put("index", bean.getMIndex());
                    jbMenuOne.put("mcss", bean.getMClass());
                    jbMenuOne.put("type", bean.getMType());
                    if ("subMenu".equals(bean.getMType())) {// ???????????????
                        JSONArray jaMenu1 = new JSONArray();
                        JSONObject jbMenuTwo = new JSONObject();
                        // ?????????????????????????????????????????????json??????
                        List<ClientMenu> listTwo = parkUserInfoMapper.findMenuList(userId, bean.getId());
                        for (ClientMenu bean1 : listTwo) {
                            jbMenuTwo.put("name", bean1.getMName());
                            jbMenuTwo.put("index", bean1.getMIndex());
                            jbMenuTwo.put("type", bean1.getMType());
                            jbMenuTwo.put("mcss", bean1.getMClass());
                            jaMenu1.add(jbMenuTwo);
                        }
                        jbMenuOne.put("children", jaMenu1);
                    }
                    jaMenu.add(jbMenuOne);
                }
            }
            // log.info(jaMenu.toString());
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", jaMenu.toString());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ?????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 15:17
     **/
    public String getMenuTreeData() {
        JSONObject jb = new JSONObject();
        try {
            JSONArray jaMenu = new JSONArray();
            // ??????????????????
            List<ClientMenu> listOne = clientMenuRepository.findFirstLevelMenu("0");
            for (ClientMenu bean : listOne) {
                JSONObject jbMenuOne = new JSONObject();
                jbMenuOne.put("id", bean.getId());
                jbMenuOne.put("label", bean.getMName());
                if ("subMenu".equals(bean.getMType())) {// ???????????????
                    JSONArray jaMenu1 = new JSONArray();
                    JSONObject jbMenuTwo = new JSONObject();
                    // ?????????????????????????????????????????????json??????
                    List<ClientMenu> listTwo = clientMenuRepository.findFirstLevelMenu(bean.getId());
                    for (ClientMenu bean1 : listTwo) {
                        jbMenuTwo.put("id", bean1.getId());
                        jbMenuTwo.put("label", bean1.getMName());
                        jaMenu1.add(jbMenuTwo);
                    }
                    jbMenuOne.put("children", jaMenu1);
                }
                jaMenu.add(jbMenuOne);
            }
            // log.info(jaMenu.toString());
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", jaMenu.toString());
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 15:51
     **/
    public String queryRoleList() {
        JSONObject jb = new JSONObject();
        List<ClientRole> list = clientRoleRepository.queryRoleList();
        try {
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", list);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/6 11:32
     **/
    public String addRoleInfo(ClientRole info) {
        JSONObject jb = new JSONObject();
        try {
            clientRoleRepository.save(info);
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 16:03
     **/
    public String deleteRoleInfo(String id) {
        JSONObject jb = new JSONObject();
        try {
            // ????????????????????????
            // ????????????????????????????????????????????????????????????
            List<UserRole> roles = userRoleRepository.findUserRoleByRoleId(id);
            if (roles.size() > 0) {
                jb.put("code", "1");
                jb.put("message", "????????????????????????????????????????????????");
                return jb.toString();
            }
            ClientRole role = clientRoleRepository.findInfoByRoleid(id);
            if (null != role) {
                clientRoleRepository.delete(role);
                // ?????????????????????
                roleLimitRepository.deleteByRoleId(id);
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/2 16:25
     **/
    public String saveRoleMenu(String id, String roleMenu) {
        JSONObject jb = new JSONObject();
        try {
            ClientRole role = clientRoleRepository.findInfoByRoleid(id);
            if (null != role) {
                role.setRoleMenu(roleMenu);
                clientRoleRepository.save(role);
                // ???????????????
                roleLimitRepository.deleteByRoleId(id);
                // ????????????
                String[] menuArr = roleMenu.split(",");
                for (int i = 0; i < menuArr.length; i++) {
                    RoleLimit rl = new RoleLimit();
                    rl.setRoleId(role.getId());
                    rl.setMenuId(menuArr[i]);
                    roleLimitRepository.save(rl);
                }
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/4/6 9:37
     **/
    public String saveUserRoleData(String userid, String transValue) {
        JSONObject jb = new JSONObject();
        try {
            // ??????????????????
            ParkUserInfo user = parkUserInfoRepository.findParkUserInfoByUserid(userid);
            if (null != user) {
                // ????????????????????????
                user.setUserRole(transValue);
                parkUserInfoRepository.save(user);
                // ??????????????????????????????
                userRoleRepository.deleteRoleByUserId(userid);
                // ????????????
                String[] roleArr = transValue.split(",");
                for (int i = 0; i < roleArr.length; i++) {
                    UserRole ur = new UserRole();
                    ur.setUserId(userid);
                    ur.setRoleId(roleArr[i]);
                    userRoleRepository.save(ur);
                }
            }
            jb.put("code", "0");
            jb.put("message", "????????????");
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ?????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * ????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/2 10:01
     **/
    public Page<OrderInfo> queryOrdersForExport(String startDate, String endDate, String startDate1, String endDate1, String plateNo, String orderState, String regionCode) {
        JSONObject jb = new JSONObject();
        Page<OrderInfo> pages = parkChargeInfoMapper.queryOrders(startDate, endDate, plateNo, orderState, regionCode, startDate1, endDate1);
        return pages;
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 9:09
     **/
    public List<DetectInfo> queryDetectList4Export(String startDate, String endDate, String plateNo, String direction) {
        Page<DetectInfo> pages = parkChargeInfoMapper.queryDetectList(startDate, endDate, plateNo, direction);
        return pages.getResult();
    }

    /**
     * ??????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 9:40
     **/
    public List<OrderPayInfo> queryOrderPay4Export(String startDate, String endDate, String plateNo, String payType, String userid) {
        Page<OrderPayInfo> pages = parkChargeInfoMapper.queryPayList(startDate, endDate, plateNo, payType, userid);
        return pages.getResult();
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 15:22
     **/
    public List<DelCars> queryCarsdel4Export(String startDate, String endDate, String plateNo, String userid, String regionCode) {
        Page<DelCars> pages = parkChargeInfoMapper.queryDelOrderList(startDate, endDate, plateNo, userid, regionCode);
        return pages.getResult();
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/3 17:52
     **/
    public List<PoleControl> exportManaulPoleList(String startDate, String endDate, String userid) {
        Page<PoleControl> pages = parkChargeInfoMapper.queryPoleList(startDate, endDate, userid);
        return pages.getResult();
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 9:00
     **/
    public List<FreeGos> exportFreeGoList(String startDate, String endDate, String plateNo, String userid) {
        Page<FreeGos> pages = parkChargeInfoMapper.queryFreeGoList(startDate, endDate, plateNo, userid);
        return pages.getResult();
    }

    /**
     * ????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 9:23
     **/
    public List<CarnoLogBean> exportCarnoList(String startDate, String endDate, String userid) {
        Page<CarnoLogBean> pages = parkChargeInfoMapper.queryCarnoList(startDate, endDate, userid);
        return pages.getResult();
    }

    /**
     * ???????????????????????????
     *
     * @Author songqiang
     * @Description
     * @Date 2021/6/4 11:03
     **/
    public String queryParkLeaguerCars(String vipId) {
        JSONObject jb = new JSONObject();
        List<ParkLeaguerCars> list = parkLeaguerCarsRepository.findByLeaguerId(vipId);
        try {
            jb.put("code", "0");
            jb.put("message", "????????????");
            jb.put("data", list);
            return jb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ???????????????????????????????????? ==");
            jb.put("code", "99");
            jb.put("message", "Exception???" + e.getMessage());
            jb.put("data", new JSONArray());
            return jb.toString();
        }
    }
}
