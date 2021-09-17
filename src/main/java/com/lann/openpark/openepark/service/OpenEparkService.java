package com.lann.openpark.openepark.service;

import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.bean.ParkVipBean;
import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.entiy.EquipInfo;
import com.lann.openpark.camera.dao.entiy.GateMsg;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.mapper.ParkVipInfoMapper;
import com.lann.openpark.camera.dao.repository.*;
import com.lann.openpark.camera.threads.UpLoadDriveOutThread;
import com.lann.openpark.camera.threads.VoiceLedThread;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.charge.service.ChargeService;
import com.lann.openpark.client.dao.entiy.ParkRegionInfo;
import com.lann.openpark.client.dao.repository.ParkRegionInfoRepository;
import com.lann.openpark.client.service.PlatenullService;
import com.lann.openpark.common.Constant;
import com.lann.openpark.common.ParkCommon;
import com.lann.openpark.openepark.dao.entiy.OrderPay;
import com.lann.openpark.openepark.dao.entiy.RefundInfo;
import com.lann.openpark.openepark.dao.repository.OrderPayRepository;
import com.lann.openpark.openepark.dao.repository.RefundInfoRepository;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.mapper.ParkChargeInfoMapper;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.order.service.Orderservice;
import com.lann.openpark.park.service.ParkService;
import com.lann.openpark.util.Base64Util;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.SignatureUtil;
import com.lann.openpark.websocket.WebSocketServer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * 停车场域openEpark对接类
 *
 * @Author songqiang
 * @Description
 * @Date 2020/7/10 15:11
 **/
@Service
public class OpenEparkService {

    private static final Logger log = LoggerFactory.getLogger(OpenEparkService.class);

    private static final String LEAVE_STATE = "8102";

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;
    @Autowired
    ParkChargeInfoMapper parkChargeInfoMapper;
    @Autowired
    ParkDetectInfoRepository parkDetectInfoRepository;
    @Autowired
    CameraDataRepository cameraDataRepository;
    @Autowired
    OrderPayRepository orderPayRepository;
    @Autowired
    EquipSendMsgRepository equipSendMsgRepository;
    @Autowired
    RefundInfoRepository refundInfoRepository;
    @Autowired
    ManualTriggerRepository manualTriggerRepository;
    @Autowired
    EquipInfoRepository equipInfoRepository;
    @Autowired
    LedConfigRepository ledConfigRepository;
    @Autowired
    VoiceConfigRepository voiceConfigRepository;
    @Autowired
    EquipInfoMapper equipInfoMapper;

    @Autowired
    ChargeService chargeService;
    @Autowired
    Orderservice orderservice;
    @Autowired
    ParkService parkService;
    @Autowired
    ParkVipInfoMapper parkVipInfoMapper;

    @Autowired
    ParkRegionInfoRepository parkRegionInfoRepository;

    @Autowired
    PlatenullService platenullService;

    @Value("${parkInfo.isVoice}")
    private boolean isVoice;//


    private static final int version = 21;// 对接文档版本

    /**
     * 测试代码
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 8:22
     **/
    public static void main(String[] args) {

    }

    //======================好停车上行对接==================================

    /**
     * 服务器对接验证接口
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/9 14:45
     **/
    public String verifyServer() {

        // 从缓存中获取serverUrl
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String servUrl = properties.getProperty("serv_url");
        String domainId = properties.getProperty("domain_id");
        String domainKey = properties.getProperty("domain_key");
        String openEparkUrl = properties.getProperty("open_epark_url");

        // 准备参数和签名
        long timeStamp = new Date().getTime();// 获得时间戳
        // 需要签名的数据
        String[] singArr = new String[]{domainId, String.valueOf(timeStamp), String.valueOf(version), servUrl};
        // 对数据进行签名
        String sign = SignatureUtil.sign(domainKey, singArr);
        // 请求openEpark
        String url = openEparkUrl + Constant.VERIFY_URL;
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
        body.add("domainId", domainId);
        body.add("timeStamp", String.valueOf(timeStamp));
        body.add("version", String.valueOf(version));
        body.add("sign", String.valueOf(sign));
        body.add("servUrl", String.valueOf(servUrl));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
        String jsonString = retbody.getBody();
        return jsonString;
    }

    /**
     * 停车场域绑定
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/9 15:58
     **/
    public String bindPark() {

        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String parkCode = properties.getProperty("park_code");
        String domainId = properties.getProperty("domain_id");
        String domainKey = properties.getProperty("domain_key");
        String openEparkUrl = properties.getProperty("open_epark_url");

        // 准备参数和签名
        long timeStamp = new Date().getTime();// 获得时间戳
        // 需要签名的数据
        String[] singArr = new String[]{domainId, String.valueOf(timeStamp), parkCode};
        // 对数据进行签名
        String sign = SignatureUtil.sign(domainKey, singArr);
        // 请求openEpark
        String url = openEparkUrl + Constant.BIND_URL;
        LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
        body.add("domainId", domainId);
        body.add("timeStamp", String.valueOf(timeStamp));
        body.add("parks", parkCode);
        body.add("sign", sign);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
        String jsonString = retbody.getBody();
        return jsonString;
    }

    /**
     * 停车场泊位数据上传
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/9 16:54
     **/
    @Transactional
    public void uploadSpaceInfo() {

        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String parkCode = properties.getProperty("park_code");
        String domainId = properties.getProperty("domain_id");
        String domainKey = properties.getProperty("domain_key");
        String openEparkUrl = properties.getProperty("open_epark_url");
        // log.info("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 泊位数据上传==");
        try {

            int berthCount = 0;
            int remainBerthCount = 0;
            // 查询region信息，获取车位数和剩余车位数
            List<ParkRegionInfo> list = parkRegionInfoRepository.findParkRegionInfoList();
            for (ParkRegionInfo region : list) {
                berthCount = berthCount + region.getBerthCount();// 总车位数
                remainBerthCount = remainBerthCount + region.getRegionRectifyCount();// 剩余车位数
            }
            // log.info("===泊位数据上传==剩余车位数：" + remainBerthCount + "，总车位数：" + berthCount + "====");
            // 拼接content
            JSONObject jb = new JSONObject();
            jb.put("method", "spaceInfo");// 上传方法
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("total", berthCount);
            params.put("remain", remainBerthCount);
            params.put("bookCount", 0);
            params.put("bookRemain", 0);
            params.put("chargeSpaceCount", 0);
            params.put("chargeSpaceRemain", 0);
            jb.put("params", params);

            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            // log.info("=== 泊位数据上传 === " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 泊位数据上传异常==");
        }
    }

    /**
     * 驶入记录上传任务
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 11:19
     **/
    @Transactional
    public void uploadDriveInTask() {
        // 生成随机数
        String random = UUID.randomUUID().toString().replace("-", "");
        // 更新需要上传驶入的随机数
        int count = parkChargeInfoMapper.updateDriveInRandom(random);
        if (count > 0) {// 有则上传，没有则忽略
            List<ParkChargeInfo> list_charge = parkChargeInfoRepository.findOrderInByRandom(random);
            if (list_charge.size() > 0) {
                for (int i = 0; i < list_charge.size(); i++) {// 循环订单信息，上传驶入信息
                    ParkChargeInfo parkChargeInfo = list_charge.get(i);
                    // 获取驶入订单号
                    String entNId = parkChargeInfo.getEnterNid();
                    // 根据entId查询驶入驶离数据，相机原始数据（主要是base64的图片）
                    // ParkDetectInfo parkDetectInfo = parkDetectInfoRepository.findParkDetectInfoByNid(entNId);
                    CameraData cameraData = cameraDataRepository.findCameraDataByNid(entNId);
                    // 上传驶入数据
                    this.uploadDriveIn(parkChargeInfo, cameraData);
                }
            }
        }
    }

    /**
     * 驶离记录上传任务
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 17:41
     **/
    @Transactional
    public void uploadDriveOutTask() {
        // 生成随机数
        String random = UUID.randomUUID().toString().replace("-", "");
        // 更新需要上传驶入的随机数
        int count = parkChargeInfoMapper.updateDriveOutRandom(random);
        if (count > 0) {// 有则上传，没有则忽略
            List<ParkChargeInfo> list_charge = parkChargeInfoRepository.findOrderOutByRandom(random);
            if (list_charge.size() > 0) {
                for (int i = 0; i < list_charge.size(); i++) {// 循环订单信息，上传驶入信息
                    ParkChargeInfo parkChargeInfo = list_charge.get(i);
                    // 获取驶入订单号
                    String exitNId = parkChargeInfo.getExitNid();
                    CameraData cameraData = cameraDataRepository.findCameraDataByNid(exitNId);
                    // 上传驶离数据
                    this.uploadDriveOut(parkChargeInfo, cameraData, LEAVE_STATE);
                }
            }
        }
    }

    /**
     * 支付记录上传任务
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/14 9:40
     **/
    @Transactional
    public void uploadPayDetailTask() {
        // 生成随机数
        String random = UUID.randomUUID().toString().replace("-", "");
        // 更新需要上传驶入的随机数
        int count = parkChargeInfoMapper.updatePayDetailRandom(random);
        List<ParkChargeInfo> list_charge = parkChargeInfoRepository.findOrderPayByRandom(random);
        if (list_charge.size() > 0) {
            for (int i = 0; i < list_charge.size(); i++) {// 循环订单信息，上传驶入信息
                ParkChargeInfo parkChargeInfo = list_charge.get(i);
                // 获取驶入订单号
                String exitNId = parkChargeInfo.getExitNid();
                CameraData cameraData = cameraDataRepository.findCameraDataByNid(exitNId);
                // 上传支付记录
                this.uploadPayDetail(parkChargeInfo, null, cameraData);
            }
        }
    }

    /**
     * 设备信息上传任务
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/17 10:13
     **/
    @Transactional
    public void uploadDeviceInfoTask() {
        // 查询设备信息列表
        List<EquipInfo> list_equip = equipInfoRepository.findEquipList();
        if (list_equip.size() > 0) {
            this.uploadDeviceInfo(list_equip);
        }
    }

    /**
     * 驶入数据上传网络请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 10:32
     **/
    public void uploadDriveIn(ParkChargeInfo parkChargeInfo, CameraData cameraData) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");
            String suffix = properties.getProperty("file_upload_suffix");

            // 驶入时间
            Date timeIn = parkChargeInfo.getCollectiondate1() == null ? new Date() : parkChargeInfo.getCollectiondate1();
            JSONObject jb = new JSONObject();
            jb.put("method", "driveIn");// 驶入记录上传
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("orderNo", parkChargeInfo.getNid());
            params.put("plateNo", parkChargeInfo.getCarno());
            params.put("plateType", parkChargeInfo.getLicensetype());
            params.put("timeIn", DateUtil.formatDateYMDHMS(timeIn));
            params.put("spaceCode", "");
            params.put("imgSuffix", suffix);
            if (null == cameraData.getImageFile()) {
                params.put("imageCount", 0);
                params.put("images", new JSONArray());
            } else {
                params.put("imageCount", 1);
                JSONArray ja = new JSONArray();
                ja.add(Base64Util.byteArrToBase64(cameraData.getImageFile()));
                params.put("images", ja);
            }
            jb.put("params", params);

            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【驶入记录上传】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==返回信息==" + jsonString);
            // 判断是否上传成功
//            JSONObject jbRet = JSONObject.fromObject(jsonString);
//            if ("success".equals(jbRet.getString("result"))) {// 失败更新订单信息
//                parkChargeInfo.setInUpload(Constant.UPLOAD_SUCCESS);// 更新上传成功标志
//            } else {
//                // 这个是使用timer定时器上传的时候，上传5次使用
//                // parkChargeInfo.setInUpload(parkChargeInfo.getInUpload() + 1);// 上传次数加1
//                // 这个是在驶入时候上传，更新失败标志为“11”
//                parkChargeInfo.setInUpload(11);// 上传失败
//            }
//            parkChargeInfoRepository.save(parkChargeInfo);// 保存
            return;
        } catch (Exception e) {
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【驶入记录上传】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==异常信息==" + e.getMessage());
            return;
        }
    }

    /**
     * 驶离数据上传网络请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 14:45
     **/
    public String uploadDriveOut(ParkChargeInfo parkChargeInfo, CameraData cameraData, String leaveState) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");
            String suffix = properties.getProperty("file_upload_suffix");

            // 驶离时间
            Date timeIn = parkChargeInfo.getCollectiondate1() == null ? new Date() : parkChargeInfo.getCollectiondate2();
            JSONObject jb = new JSONObject();
            jb.put("method", "driveOut");// 驶入记录上传
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("orderNo", parkChargeInfo.getNid());
            params.put("totalCost", parkChargeInfo.getTotalcharge());
            // params.put("realCost", parkChargeInfo.getRealcharge());
            params.put("realCost", parkChargeInfo.getTotalcharge());
            params.put("timeOut", DateUtil.formatDateYMDHMS(timeIn));
            params.put("parkDuration", parkChargeInfo.getParkDuration());
            params.put("leaveState", leaveState);
            params.put("imgSuffix", suffix);
            if (null == cameraData || null == cameraData.getImageFile()) {
                params.put("imageCount", 0);
                params.put("images", new JSONArray());
                JSONArray ja = new JSONArray();
                params.put("images", ja);
            } else {
                params.put("imageCount", 1);
                JSONArray ja = new JSONArray();
                ja.add(Base64Util.byteArrToBase64(cameraData.getImageFile()));
                params.put("images", ja);
            }
            jb.put("params", params);

            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
//            log.info("【驶离记录上传】" + jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【出口设备号】：" + parkChargeInfo.getDevicecodeExt() +
                    " ==【驶离记录上传】==车牌号==" + parkChargeInfo.getCarno() + "==orderNo==" + parkChargeInfo.getNid()
                    + "==返回信息==" + jsonString);
            // 判断是否上传成功
//            JSONObject jbRet = JSONObject.fromObject(jsonString);
//            if ("success".equals(jbRet.getString("result"))) {// 失败更新订单信息
//                parkChargeInfo.setOutUpload(Constant.UPLOAD_SUCCESS);// 更新上传成功标志
//            } else {
//                // 这个是使用timer定时器上传的时候，上传5次使用
//                // parkChargeInfo.setOutUpload(parkChargeInfo.getOutUpload() + 1);// 上传次数加1
//                // 这个是在驶入时候上传，更新失败标志为“11”
//                parkChargeInfo.setOutUpload(11);// 上传失败
//            }
//            parkChargeInfoRepository.save(parkChargeInfo);// 保存
            return jsonString;
        } catch (Exception e) {
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【出口设备号】：" + parkChargeInfo.getDevicecodeExt() +
                    " ==【驶离记录上传】==车牌号==" + parkChargeInfo.getCarno() + "==orderNo==" + parkChargeInfo.getNid()
                    + "==异常信息==" + e.getMessage());
            JSONObject jb = new JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "驶离记录上传异常");
            return jb.toString();
        }
    }


    /**
     * 支付记录上传网络请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 17:54
     **/
    public void uploadPayDetail(ParkChargeInfo parkChargeInfo, OrderPay orderPay, CameraData cameraData) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");

            String orderNo = parkChargeInfo.getNid();// 订单编号
            // 支付记录上传
            // 考虑场内付，多次支付的场景，支付金额和支付方式循环支付表上传
            // mod by sq 20201203
            // 驶离时间
            JSONObject jb = new JSONObject();
            jb.put("method", "payDetail");// 支付记录上传
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("orderNo", orderNo);

            if (null != orderPay) {
                params.put("payFee", orderPay.getPayFee());
                params.put("payType", orderPay.getPayType());
                params.put("dereateFee", orderPay.getDerateFee() + orderPay.getCouponFee());
                params.put("payTime", DateUtil.formatDateYMDHMS(orderPay.getPayTime()));
            } else {
                params.put("payFee", parkChargeInfo.getCharge());
                params.put("payType", parkChargeInfo.getPayType());
                params.put("dereateFee", parkChargeInfo.getDerateFee() + parkChargeInfo.getCouponFee());
                params.put("payTime", DateUtil.formatDateYMDHMS(parkChargeInfo.getPayTime()));
            }


            jb.put("params", params);

            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【支付记录上传】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==返回信息==" + jsonString);

        } catch (Exception e) {
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【支付记录上传】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==异常信息==" + e.getMessage());
            return;
        }
    }

    /**
     * 出入口设备信息上传网络请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 17:54
     **/
    private void uploadDeviceInfo(List<EquipInfo> list_equip) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");

            JSONObject jb = new JSONObject();
            jb.put("method", "uploadDeviceInfo");// 出入口设备信息上传
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            JSONArray ja = new JSONArray();
            for (EquipInfo equipInfo : list_equip) {
                JSONObject jb_equip = new JSONObject();
                jb_equip.put("pointCode", equipInfo.getPointcode());// 出入口编号
                jb_equip.put("pointName", equipInfo.getDevicename());// 设备名称
                jb_equip.put("direction", equipInfo.getVideofunc());// 出入口方向
                jb_equip.put("deviceCode", equipInfo.getDevicecode());// 设备编号
                jb_equip.put("deviceType", Constant.EQUIP_TYPE_CAMERA);// 设备类型，智能相机
                jb_equip.put("deviceMain", "1");// 主设备
                ja.add(jb_equip);
            }
            params.put("deviceList", ja);
            jb.put("params", params);

            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【出入口设备信息上传】==返回信息==" + jsonString);
            return;
        } catch (Exception e) {
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【出入口设备信息上传】==异常信息==" + e.getMessage());
            return;
        }
    }

    //======================好停车上行对接==================================


    //======================好停车下行对接==================================

    /**
     * 停车场下行统一处理方法
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 15:21
     **/
    public String receiveHandler(String bodyString) {
        // 解析下发内容
        try {
            JSONObject jb = JSONObject.fromObject(bodyString);
            String method = jb.getString("method");
            if ("payDetail".equals(method)) {// 接收支付详情下发
                return this.receivePayDetail(jb);
            } else if ("queryOrderFee".equals(method)) {// 计费调用（根据订单编号）
                return this.receiveQueryOrderFee(jb);
            } else if ("queryOrderFeeByParkTime".equals(method)) {// 计费调用（时间、车牌）
                return this.receiveQueryOrderFeeByParkTime(jb);
            } else if ("updateOrderInfo4Complaint".equals(method)) {// 订单投诉
                return this.receiveUpdateOrderInfo4Complaint(jb);
            } else if ("manualPlateCorrect".equals(method)) {// 号牌纠正
                return this.receiveManualPlateCorrect(jb);
            } else if ("driveInNotify".equals(method)) {// 通知驶入
                return this.receiveDriveInNotify(jb);
            } else if ("driveOutNotify".equals(method)) {// 通知驶入
                return this.receiveDriveOutNotify(jb);
            } else if ("updateOrderInfo".equals(method)) {// 订单修改
                return this.receiveUpdateOrderInfo(jb);
            } else if ("getThirdOrderByParkCodeAndOther".equals(method)) {// 根据设备ID获取当前订单信息
                return this.receiveGetThirdOrderByParkCodeAndOther(jb);
            } else if ("gateBarrierControl".equals(method)) {// 远程抬杆
                return this.receiveGateBarrierControl(jb);
            } else if ("notifyParkOrderCoupon".equals(method)) {// 优惠券下发
                return this.receiveNotifyParkOrderCoupon(jb);
            } else {
                JSONObject ret = new JSONObject();
                ret.put("result", "fail");
                ret.put("msg", "不支持的接口方法");
                return ret.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("接收openEpark下行数据异常：" + e.getMessage());
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "exception");
            return ret.toString();
        }
    }

    /**
     * 接收支付详情下发
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 15:32
     **/
    private String receivePayDetail(JSONObject jb) throws Exception {
        log.info("接收支付记录的内容：" + jb.toString());
        String parkCode = jb.getString("parkCode");
        String orderNo = jb.getString("orderNo");// 订单编号
        Double payFee = jb.getDouble("payFee");// 支付费用
        String payType = jb.getString("payType");// 支付方式
        String outTradeNo = jb.getString("outTradeNo");// 支付流水号
        Double derateFee = jb.getDouble("derateFee");// 打折费用
        Double couponFee = jb.getDouble("couponFee");// 优惠券费用
        String payTime = jb.getString("payTime");// 支付时间
        // 根据订单号，查询订单信息
        ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
        if (null == parkChargeInfo) {// 没有查询到订单信息，直接返回成功
            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            ret.put("responseBody", new JSONArray());
            return ret.toString();
        }

        // 插入订单支付信息
        OrderPay orderPay = new OrderPay();
        orderPay.setParkCode(parkCode);
        orderPay.setOrderNo(orderNo);
        orderPay.setCouponFee(couponFee.floatValue());
        orderPay.setDerateFee(derateFee.floatValue());
        orderPay.setOutTradeNo(outTradeNo);
        orderPay.setPayFee(payFee.floatValue());
        orderPay.setPayType(payType);
        if (StringUtils.isNotEmpty(payTime)) {// 支付时间
            orderPay.setPayTime(DateUtil.toDateTime(payTime));
        }
        orderPay.setInsertTime(new Date());
        orderPayRepository.save(orderPay);

        // 查看订单是否有驶离信息
        if (null == parkChargeInfo.getExitNid() || parkChargeInfo.getExitNid().equals("")) {
            // 1.车辆未驶离，车主就扫码缴费了
            // 2.出口车辆速度太快，导致未识别驶离，车主扫码，而且费用>0，车主缴费
            // 如果不处理第二种情况，导致不抬杆，订单结束，上传驶离失败。车辆需要后退一次，识别，无订单出场
            // 需要修改，暂记费用。车辆后退，再做离场
            log.info("车辆：" + parkChargeInfo.getCarno() + "提前缴费，或者出场未识别到...");
            // 更新订单费用信息
            float charge = parkChargeInfo.getCharge() + payFee.floatValue();
            parkChargeInfo.setCharge(charge);// 更新实交费用
            parkChargeInfo.setPayType(payType);
            float derate_fee = parkChargeInfo.getDerateFee() + derateFee.floatValue();
            parkChargeInfo.setDerateFee(derate_fee);
            float coupon_fee = parkChargeInfo.getCouponFee() + couponFee.floatValue();
            parkChargeInfo.setCouponFee(coupon_fee);
            if (StringUtils.isNotEmpty(payTime)) {// 支付时间
                parkChargeInfo.setPayTime(DateUtil.toDateTime(payTime));
            }
            // 更新订单
            parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);
            // 返回处理成功信息
            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            ret.put("responseBody", new JSONArray());
            return ret.toString();
        }

        // 更新订单信息
        float charge = parkChargeInfo.getCharge() + payFee.floatValue();
        parkChargeInfo.setCharge(charge);// 更新实交费用
        parkChargeInfo.setPayType(payType);
        float derate_fee = parkChargeInfo.getDerateFee() + derateFee.floatValue();
        parkChargeInfo.setDerateFee(derate_fee);
        float coupon_fee = parkChargeInfo.getCouponFee() + couponFee.floatValue();
        parkChargeInfo.setCouponFee(coupon_fee);
        parkChargeInfo.setPayUpload(0);// 支付记录可以上传
        if (StringUtils.isNotEmpty(payTime)) {// 支付时间
            parkChargeInfo.setPayTime(DateUtil.toDateTime(payTime));
        }

        // 判断是否插入开闸消息
        if (parkChargeInfo.getCharge() + parkChargeInfo.getDerateFee() >= parkChargeInfo.getTotalcharge()) {

            // 获取缓存读取工具
            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            // 读取ETC配置
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            // 桌面推送
            String isDesk = properties.getProperty("is_desk");
            if ("1".equals(isDesk)) {
                WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_PAY_MESSAGE, Constant.getPayTypeName(payType), String.valueOf(parkChargeInfo.getCharge()));
            }

            parkChargeInfo.setExitType(Constant.EXIT_TYPE_AUTO);// 修改欠费驶离为自动放行
            parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);

            List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(parkChargeInfo.getDevicecodeExt());
            if (list_equip.size() > 0) {
                EquipGateInfo equipGateInfo = list_equip.get(0);
                //                EquipSendMsg equipSendMsg = new EquipSendMsg();// 生成下发消息
                //                equipSendMsg.setDevicecode(equipGateInfo.getDevicecode());// 离场设设备编号
                //                equipSendMsg.setMsgType(1);// msg_type 1 开闸
                //                equipSendMsg.setInsertTime(new Date());
                //                equipSendMsgRepository.save(equipSendMsg);

                GateMsg gateMsg = new GateMsg();
                gateMsg.setDevicecode(equipGateInfo.getDevicecode());
                // 写缓存
                ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);

                // 是否下发语音
                if (isVoice) {
                    // 下发语音和LED屏显消息
                    // voiceType = 7
                    // ledType = 4
                    VoiceLedThread thread = new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 7, 4, null, 1000, charge);
                    thread.run();
                }

            }

            CameraData cameraData = cameraDataRepository.findCameraDataByNid(parkChargeInfo.getExitNid());
            // 上传驶离
            Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, cameraData, "8102"));
            upLoadDriveOutThread.start();

        } else {
            parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);
        }


        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", new JSONArray());
        log.info("【payDetail】返回：" + ret.toString());
        return ret.toString();
    }

    /**
     * 查询订单费用
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 16:27
     **/
    private String receiveQueryOrderFee(JSONObject jb) throws Exception {
        String parkCode = jb.getString("parkCode");
        JSONArray orders = jb.getJSONArray("orders");// 订单编号
        JSONArray responseBodyJa = new JSONArray();
        for (int i = 0; i < orders.size(); i++) {
            String orderNo = orders.getString(i);
            // 根据订单编号查询订单信息
            ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
            if (null != parkChargeInfo) {
                //查询订单费用
                Date timeIn = parkChargeInfo.getCollectiondate1();// 驶入时间
                Date timeOut = new Date();// 当前时间减去30秒作为驶离时间
                boolean isFreeTime = ParkCommon.getIsFreeTime(parkChargeInfo);
                String plateType = parkChargeInfo.getPlateType() == null ? "1" : parkChargeInfo.getPlateType();// 车牌类型

                // 查询出口区域信息
                List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(parkChargeInfo.getDevicecode());
                String regionCode = list_equip.get(0).getRegionCode();
                ParkingInfo parkingInfo = parkService.getParkingFee(timeIn, timeOut, Integer.valueOf(plateType), isFreeTime, regionCode);
                if ("1".equals(parkChargeInfo.getIsFristDayFree()) || "1".equals(parkChargeInfo.getIsLastDayFree())) {
                    if ("1".equals(parkChargeInfo.getIsFristDayFree())) {
                        // timeIn跳转到下一天controlgate
                        String timeInStr = DateUtil.formatDateYMD(DateUtil.addDay(timeIn, 1)) + " 00:00:00";
                        timeIn = DateUtil.parseDateYMDHMS(timeInStr);
                        // log.info("折扣后的驶入时间：" + DateUtil.formatDateYMDHMS(timeIn));
                    }
                    if ("1".equals(parkChargeInfo.getIsLastDayFree())) {
                        String timeOutStr = DateUtil.formatDateYMD(DateUtil.addDay(timeOut, -1)) + " 23:59:59";
                        timeOut = DateUtil.parseDateYMDHMS(timeOutStr);
                        // log.info("折扣后的驶离时间：" + DateUtil.formatDateYMDHMS(timeOut));
                    }
                    // 重新计算费用
                    ParkingInfo parkingInfo1 = parkService.getParkingFee(timeIn, timeOut, Integer.valueOf(plateType), isFreeTime, regionCode);
                    // 更新订单信息
                    if (parkingInfo.getCost() - parkingInfo1.getCost() > 0) {
                        // 更新折扣费用
                        parkChargeInfo.setDerateFee((float) (parkingInfo.getCost() - parkingInfo1.getCost()));
                        parkChargeInfoRepository.saveAndFlush(parkChargeInfo);
                    }
                    parkingInfo = parkingInfo1;
                }

                if ("0".equals(parkChargeInfo.getExitType()) || Constant.EXIT_TYPE_TMP.equals(parkChargeInfo.getExitType())) {// 正在进行中的订单

                    // 真实费用需要减去优惠券费用 BUG修改，mod by sq 20210629
                    // epark扫码页面计算停车费的计算方法：totalCost - hasPaid，needPay和realCost字段根本没有用到
                    // 优惠券不能放到已经支付，hasPaid是真实支付的费用，所以需要先减掉优惠券费用，totalCost - couponFee
                    double totalCost = parkingInfo.getCost() - parkChargeInfo.getCouponFee();
                    if (totalCost < 0) {
                        totalCost = 0;
                    }
                    double fee = parkingInfo.getCost() - parkChargeInfo.getCharge() - parkChargeInfo.getCouponFee();
                    if (fee < 0) {
                        fee = 0;
                    }

                    JSONObject jbOrder = new JSONObject();
                    jbOrder.put("parkCode", parkCode);
                    jbOrder.put("orderNo", orderNo);
                    jbOrder.put("parkDuration", parkingInfo.getParkLength());// 停车时长
                    jbOrder.put("totalCost", totalCost);// 停车总费用


                    jbOrder.put("realCost", fee);
                    jbOrder.put("hasPaid", parkChargeInfo.getCharge());
                    jbOrder.put("derateFee", 0);
                    jbOrder.put("protocolVersion", "1");
                    jbOrder.put("needPay", fee);

                    // 查询会员信息
                    List<ParkVipBean> list_vip = parkVipInfoMapper.findParkVipInfo(parkChargeInfo.getCarno(), String.valueOf(plateType), DateUtil.toDate("yyyy-MM-dd"));
                    if (list_vip.size() > 0) {
                        jbOrder.put("totalCost", 0);// 停车总费用
                        jbOrder.put("realCost", 0);
                        jbOrder.put("hasPaid", 0);
                        jbOrder.put("needPay", 0);
                    }

                    responseBodyJa.add(jbOrder);

                } else {// 其他状态的订单
                    JSONObject jbOrder = new JSONObject();
                    jbOrder.put("parkCode", parkCode);
                    jbOrder.put("orderNo", orderNo);
                    jbOrder.put("parkDuration", parkChargeInfo.getParkDuration());
                    jbOrder.put("totalCost", parkChargeInfo.getTotalcharge());
                    jbOrder.put("realCost", parkChargeInfo.getTotalcharge());
                    jbOrder.put("hasPaid", parkChargeInfo.getCharge());
                    jbOrder.put("derateFee", 0);
                    jbOrder.put("needPay", parkChargeInfo.getTotalcharge() - parkChargeInfo.getCharge());
                    jbOrder.put("protocolVersion", "1");
                    responseBodyJa.add(jbOrder);
                }
                if (jb.containsKey("leaguerId")) {// 判断是否有会员ID
                    if (StringUtils.isNotEmpty(jb.getString("leaguerId"))) {
                        parkChargeInfo.setEparkLeaguerId(jb.getString("leaguerId"));
                        parkChargeInfoRepository.save(parkChargeInfo);
                    }

                }
            }
        }
        // 返回信息
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", responseBodyJa);
        log.info("【queryOrderFee】返回：" + ret.toString());
        return ret.toString();
    }

    /**
     * 计费调用（时间、车牌）
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 16:58
     **/
    private String receiveQueryOrderFeeByParkTime(JSONObject jb) throws Exception {
        // String parkCode = jb.getString("parkCode");
        String orderNo = jb.getString("orderNo");// 订单编号
        String timeIn = jb.getString("timeIn");// 驶入时间
        String timeOut = jb.getString("timeOut");// 驶离时间
        // String plateNo = jb.getString("plateNo");// 车牌号
        // String plateType = jb.getString("plateType");// 车牌类型
        JSONArray responseBodyJa = new JSONArray();
        // 根据订单编号查询订单信息
        ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
        // 根据驶入驶离时间查询停车费用
        if (null != parkChargeInfo) {
            if (jb.containsKey("leaguerId")) {// 判断是否有会员ID
                if (StringUtils.isNotEmpty(jb.getString("leaguerId"))) {
                    parkChargeInfo.setEparkLeaguerId(jb.getString("leaguerId"));
                    parkChargeInfoRepository.save(parkChargeInfo);
                }
            }
            // 根据驶入驶离时间查询订单费用
            boolean isFreeTime = ParkCommon.getIsFreeTime(parkChargeInfo);
            // 查询出口区域信息
            List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(parkChargeInfo.getDevicecode());
            String regionCode = list_equip.get(0).getRegionCode();
            ParkingInfo parkingInfo = parkService.getParkingFee(DateUtil.parseDateYMDHMS(timeIn), DateUtil.parseDateYMDHMS(timeOut), Integer.valueOf(parkChargeInfo.getPlateType()), isFreeTime, regionCode);
            int parkLength = parkingInfo.getParkLength();
            double fee = parkingInfo.getCost();
            JSONObject jbOrder = new JSONObject();
            jbOrder.put("realCost", fee);// 实际支付的金额
            jbOrder.put("parkDuration", parkLength);// 停车时长
            if ("0".equals(parkChargeInfo.getExitType())) {// 未离场
                jbOrder.put("totalCost", fee);// 原始计费金额
            } else {// 其他状态
                jbOrder.put("totalCost", parkChargeInfo.getCharge());// 原始计费金额
            }
            responseBodyJa.add(jbOrder);
        }

        // 返回信息
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", responseBodyJa);
        log.info("【queryOrderFeeByParkTime】返回：" + ret.toString());
        return ret.toString();
    }

    /**
     * 订单投诉
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/10 17:26
     **/
    private String receiveUpdateOrderInfo4Complaint(JSONObject jb) throws Exception {
        // String parkCode = jb.getString("parkCode");
        String orderNo = jb.getString("orderNo");// 订单编号
        JSONArray responseBodyJa = new JSONArray();
        // 判断退款信息是否存在
        JSONObject jb_order = jb.getJSONObject("orderInfo");

        // 根据订单编号查询订单信息
        ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
        if (null != parkChargeInfo) {
            // 更新订单信息
            String plateNo = jb_order.getString("plateNo");
            String plateType = jb_order.getString("plateType");
            String timeIn = jb_order.getString("timeIn");
            String timeOut = jb_order.getString("timeOut");
            int parkDuration = jb_order.getInt("parkDuration");
            Double realCost = jb_order.getDouble("realCost");
            Double hasPaid = jb_order.getDouble("hasPaid");

            parkChargeInfo.setCarno(plateNo);
            parkChargeInfo.setLicensetype(plateType);
            if (StringUtils.isNotEmpty(timeIn)) {
                parkChargeInfo.setCollectiondate1(DateUtil.parseDateYMDHMS(timeIn));
            }
            if (StringUtils.isNotEmpty(timeOut)) {
                parkChargeInfo.setCollectiondate2(DateUtil.parseDateYMDHMS(timeOut));
            }
            parkChargeInfo.setParkDuration(parkDuration);
            parkChargeInfo.setCharge(realCost.floatValue());
            parkChargeInfo.setRealcharge(hasPaid.floatValue());
            parkChargeInfoRepository.save(parkChargeInfo);
            if (jb.containsKey("refundInfo")) {
                JSONObject jb_refund = jb.getJSONObject("refundInfo");
                double redundantRecharge = jb_refund.getDouble("redundantRecharge");// 多计费金额
                double refundFee = jb_refund.getDouble("refundFee");// 退费金额
                String refundType = jb_refund.getString("refundType");// 退费方式
                String refundTime = jb_refund.getString("refundTime");// 退费时间
                // 插入refundInfo信息
                RefundInfo refundInfo = new RefundInfo();
                refundInfo.setOrderNo(parkChargeInfo.getNid());// 订单编号
                refundInfo.setRedundantRecharge(redundantRecharge);
                refundInfo.setRefundFee(refundFee);
                refundInfo.setRefundType(refundType);
                refundInfo.setRefundTime(StringUtils.isEmpty(refundTime) ? null : DateUtil.toDateTime(refundTime));
                refundInfoRepository.save(refundInfo);
            }
        }
        // 返回信息
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", responseBodyJa);
        log.info("【updateOrderInfo4Complaint】返回：" + ret.toString());
        return ret.toString();
    }

    /**
     * 号牌纠正
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 9:25
     **/
    private String receiveManualPlateCorrect(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        // String parkCode = jb.getString("parkCode");
        String orderNo = jb.getString("orderNo");// 订单编号
        String plateNo = jb.getString("plateNo");// 车牌号码
        String plateType = jb.getString("plateType");// 车牌号码

        // 根据订单编号查询订单信息
        ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
        // 订单存在保存订单信息
        if (null != parkChargeInfo) {
            parkChargeInfo.setCarno(plateNo);
            parkChargeInfo.setLicensetype(plateType);
            parkChargeInfoRepository.save(parkChargeInfo);
        }
        // 返回信息
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", responseBodyJa);
        log.info("【manualPlateCorrect】返回：" + ret.toString());
        return ret.toString();
    }

    /**
     * 通知驶入
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 9:34
     **/
    private String receiveDriveInNotify(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        String plateNo = jb.getString("plateNo");// 车牌号码
        String licensetype = jb.getString("plateType");// 车牌类型
        String devCode = jb.getString("devCode");// 设备序列号
        String time = jb.getString("time");// 驶入时间

        // 根据设备序列号查询设备信息
        List<EquipGateInfo> equipGateInfo_list = equipInfoMapper.findEquipAndGateInfo(devCode);
        if (equipGateInfo_list.size() <= 0) {
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "未查询到设备信息");
            ret.put("responseBody", responseBodyJa);
            return ret.toString();
        }
        // EquipGateInfo equipGateInfo = equipGateInfo_list.get(0);

        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String parkCode = properties.getProperty("park_code");

        // 做无牌车驶入处理
        JSONObject json = platenullService.noplatein(parkCode, devCode, plateNo);
        // 解析返回结果
        if ("0".equals(json.getString("code"))) {
            JSONObject body = new JSONObject();
            body.put("parkCode", parkCode);
            body.put("devCode", devCode);
            body.put("allowIn", true);
            body.put("reason", "success");
            responseBodyJa.add(body);
            // 返回信息
            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            ret.put("responseBody", responseBodyJa);
            log.info("【driveInNotify】返回成功：" + ret.toString());
            return ret.toString();
        } else {// 失败
            JSONObject body = new JSONObject();
            body.put("parkCode", parkCode);
            body.put("devCode", devCode);
            body.put("allowIn", false);
            body.put("reason", json.getString("message"));
            responseBodyJa.add(body);
            // 返回信息
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", json.getString("message"));
            ret.put("responseBody", responseBodyJa);
            log.info("【driveInNotify】返回失败：" + ret.toString());
            return ret.toString();
        }
    }

    /**
     * 通知驶离
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 10:47
     **/
    private String receiveDriveOutNotify(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        String plateNo = jb.getString("plateNo");// 车牌号码
        String licensetype = jb.getString("plateType");// 车牌类型
        String devCode = jb.getString("devCode");// 设备序列号
        String time = jb.getString("time");// 驶入时间

        // 根据设备序列号查询设备信息
        List<EquipGateInfo> equipGateInfo_list = equipInfoMapper.findEquipAndGateInfo(devCode);
        if (equipGateInfo_list.size() <= 0) {
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "未查询到设备信息");
            ret.put("responseBody", responseBodyJa);
            return ret.toString();
        }

//        EquipGateInfo equipGateInfo = equipGateInfo_list.get(0);
//        // 判断是否有车牌
//        if (StringUtils.isNotEmpty(plateNo)) {
//            // 根据车牌号查询订单信息
//            List<ParkChargeInfo> list = parkChargeInfoRepository.findParkChargeInfoUnclosed(plateNo, licensetype);
//            if (list.size() > 0) {
//                ParkChargeInfo parkChargeInfo = new ParkChargeInfo();
//                parkChargeInfo.setTotalcharge(0);
//                parkChargeInfo.setCharge(0);
//                parkChargeInfo.setRealcharge(0);
//                parkChargeInfo.setDerateFee(0);
//                parkChargeInfo.setCouponFee(0);
//                parkChargeInfo.setDiscount(0);
//                parkChargeInfo.setPayType(Constant.PAY_TYPE_CASH);// 现金支付
//                parkChargeInfo.setOperator("0");
//                parkChargeInfo.setParkingType(Constant.PARK_TYPE_TMP);// 临时车
//                parkChargeInfo.setExitType(Constant.EXIT_TYPE_OPEN_EPARK);// 通知放行
//                parkChargeInfo.setPointcodeExt(equipGateInfo.getPointcode());// 出口编号
//                parkChargeInfo.setPointnameExt(equipGateInfo.getPointname());// 出口名称
//                parkChargeInfo.setDevicecodeExt(equipGateInfo.getDevicecode());// 摄像机编号
//                parkChargeInfo.setCpicExitPath("");// 驶离图片
//                parkChargeInfo.setCollectiondate2(new Date());// 驶离时间
//                parkChargeInfoRepository.saveAndFlush(parkChargeInfo);
//            }
//        }
//
//        // 放行
//        // 开闸消息插入缓存
//        GateMsg gateMsg = new GateMsg();
//        gateMsg.setDevicecode(devCode);
//        // 写缓存
//        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
//        ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + devCode, gateMsg);
//
//        // 返回信息
//        JSONObject ret = new JSONObject();
//        ret.put("result", "success");
//        ret.put("msg", "success");
//        ret.put("responseBody", responseBodyJa);
//        log.info("【driveOutNotify】返回：" + ret.toString());
//        return ret.toString();

        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
        String parkCode = properties.getProperty("park_code");

        // 做无牌车驶入处理
        JSONObject json = platenullService.noplateout(devCode, plateNo);
        // 解析返回结果
        if ("0".equals(json.getString("code"))) {
            JSONObject body = new JSONObject();
            body.put("parkCode", parkCode);
            body.put("devCode", devCode);
            body.put("allowOut", true);
            body.put("reason", "success");
            responseBodyJa.add(body);
            // 返回信息
            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            ret.put("responseBody", responseBodyJa);
            log.info("【driveInNotify】返回成功：" + ret.toString());
            return ret.toString();
        } else {// 失败
            JSONObject body = new JSONObject();
            body.put("parkCode", parkCode);
            body.put("devCode", devCode);
            body.put("allowOut", false);
            body.put("reason", json.getString("message"));
            responseBodyJa.add(body);
            // 返回信息
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", json.getString("message"));
            ret.put("responseBody", responseBodyJa);
            log.info("【driveInNotify】返回失败：" + ret.toString());
            return ret.toString();
        }

    }

    /**
     * 订单修改
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 14:37
     **/
    private String receiveUpdateOrderInfo(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        String orderNo = jb.getString("orderNo");// 订单编号
        // String parkCode = jb.getString("parkCode");// 停车场编号
        String plateNo = jb.getString("plateNo");// 车牌号码
        String plateType = jb.getString("plateType");// 车牌类型
        String timeIn = jb.getString("timeIn");// 驶入时间
        String timeOut = jb.getString("timeOut");// 驶离时间
        int parkDuration = jb.getInt("parkDuration");// 驶离时间
        Double realCost = jb.getDouble("realCost");// 订单实际费用
        String orderState = jb.getString("orderState");// 订单状态

        // 根据订单编号查询订单信息
        ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(orderNo);
        if (null != parkChargeInfo) {// 订单存在，更新订单信息
            parkChargeInfo.setCarno(plateNo);
            parkChargeInfo.setLicensetype(plateType);
            if (StringUtils.isNotEmpty(timeIn)) {
                parkChargeInfo.setCollectiondate1(DateUtil.parseDateYMDHMS(timeIn));
            }
            if (StringUtils.isNotEmpty(timeOut)) {
                parkChargeInfo.setCollectiondate2(DateUtil.parseDateYMDHMS(timeOut));
            }
            parkChargeInfo.setParkDuration(parkDuration);
            parkChargeInfo.setRealcharge(realCost.floatValue());
            if ("1101".equals(orderState)) {
                parkChargeInfo.setExitType("0");
            } else if ("1102".equals(orderState)) {
                parkChargeInfo.setExitType(Constant.EXIT_TYPE_TMP);// 欠费驶离
            } else if ("1103".equals(orderState)) {
                parkChargeInfo.setExitType(Constant.EXIT_TYPE_OPEN_EPARK);// 订单结束
            }
            parkChargeInfoRepository.save(parkChargeInfo);
        }

        // 返回信息
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", responseBodyJa);
        log.info("【updateOrderInfo】返回：" + ret.toString());
        return ret.toString();
    }

    /**
     * 根据设备ID号，查询当前出口的车辆
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/13 15:49
     **/
    private String receiveGetThirdOrderByParkCodeAndOther(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        // String parkCode = jb.getString("parkCode");// 停车场编号
        String deviceNo = jb.getString("deviceNo");// 车牌号码

        // 插入手动识别消息
        //        EquipSendMsg equipSendMsg = new EquipSendMsg();
        //        equipSendMsg.setDevicecode(deviceNo);
        //        equipSendMsg.setMsgType(2);
        //        equipSendMsg.setInsertTime(new Date());
        //        equipSendMsgRepository.save(equipSendMsg);

//        // 将消息插入缓存
//        GateMsg gateMsg = new GateMsg();
//        gateMsg.setDevicecode(deviceNo);
//        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
//        ehcacheUtil.put(Constant.PARK_CACHE, "manual_trigger_" + deviceNo, gateMsg);
//
//        List<ManualTrigger> list_manual_trigger = new ArrayList<>();
//        String random = UUID.randomUUID().toString().replace("-", "");
//        Thread.sleep(100);
//        for (int i = 0; i < 5; i++) {
//            log.info("【手动识别】返回结果：第" + (i + 1) + "次");
//            int num = manualTriggerRepository.updateManualTriggerByRandom(random, deviceNo);
//            if (num > 0) {
//                list_manual_trigger = manualTriggerRepository.findManualTriggerByRandom(random);
//                break;
//            } else {
//                Thread.sleep(100);
//            }
//        }
//
//        if (list_manual_trigger.size() <= 0) {
//            JSONObject ret = new JSONObject();
//            ret.put("result", "fail");
//            ret.put("msg", "停车场识别错误");
//            ret.put("responseBody", responseBodyJa);
//            return ret.toString();
//        }
//        ManualTrigger manualTrigger = list_manual_trigger.get(0);
//        if (manualTrigger.getPlateNo().equals("-无-")) {
//            JSONObject ret = new JSONObject();
//            ret.put("result", "fail");
//            ret.put("msg", "未识别到待离场的车辆信息");
//            ret.put("responseBody", responseBodyJa);
//            return ret.toString();
//        }
//        if (StringUtils.isEmpty(manualTrigger.getOrderNo())) {
//            JSONObject ret = new JSONObject();
//            ret.put("result", "fail");
//            ret.put("msg", "未查询到识别订单信息");
//            ret.put("responseBody", responseBodyJa);
//            return ret.toString();
//        }
//
//        JSONObject jbRet = new JSONObject();
//        jbRet.put("method", "getThirdOrderByParkCodeAndOther");
//        jbRet.put("orderNo", manualTrigger.getOrderNo());
//        jbRet.put("plateNo", manualTrigger.getPlateNo());
//        jbRet.put("plateType", manualTrigger.getPlateType());
//        responseBodyJa.add(jbRet);

        // 根据设备编号查询最近一次识别记录
        CameraData cameraData = cameraDataRepository.findLastCameraDataByDeviceNo(deviceNo);

        if (null == cameraData) {
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "未查询到识别信息");
            ret.put("responseBody", responseBodyJa);
            return ret.toString();
        }

        Date date = new Date();// 当前时间

        if (null == cameraData.getInsertTime()) {
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "识别信息异常");
            ret.put("responseBody", responseBodyJa);
            return ret.toString();
        }

        // 比较识别时间与当前时间，如当前时间与识别时间相差120秒之内
        Date camDate = cameraData.getInsertTime();
        if (date.getTime() - camDate.getTime() < 5 * 60 * 1000) {
            String plateNo = cameraData.getLicense();
            int plateType = cameraData.getPlateType();// 车牌类型
            // 根据车牌号码和车牌类型查询订单信息
            List<ParkChargeInfo> list_parkChargeInfo = orderservice.findOutCarOrder(plateNo, String.valueOf(plateType));
            if (list_parkChargeInfo.size() <= 0) {
                JSONObject ret = new JSONObject();
                ret.put("result", "fail");
                ret.put("msg", "未查询到订单信息");
                ret.put("responseBody", responseBodyJa);
                return ret.toString();
            }
            ParkChargeInfo parkChargeInfo = list_parkChargeInfo.get(0);
            if (!(parkChargeInfo.getExitType().equals("0") || parkChargeInfo.getExitType().equals("7"))) {
                JSONObject ret = new JSONObject();
                ret.put("result", "fail");
                ret.put("msg", "订单已经结束");
                ret.put("responseBody", responseBodyJa);
                return ret.toString();
            }
            JSONObject jbRet = new JSONObject();
            jbRet.put("method", "getThirdOrderByParkCodeAndOther");
            jbRet.put("orderNo", parkChargeInfo.getNid());
            jbRet.put("plateNo", plateNo);
            jbRet.put("plateType", parkChargeInfo.getLicensetype());
            log.info("epark请求正在付费的订单车牌返回：" + plateNo);
            responseBodyJa.add(jbRet);
        } else {
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "未查询到识别信息");
            ret.put("responseBody", responseBodyJa);
            return ret.toString();
        }


        // 返回信息
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", responseBodyJa);
        return ret.toString();

//        JSONObject ret = new JSONObject();
//        ret.put("result", "fail");
//        ret.put("msg", "未对接");
//        ret.put("responseBody", responseBodyJa);
//        return ret.toString();
    }

    /**
     * 远程抬杆
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/20 15:00
     **/
    private String receiveGateBarrierControl(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        String parkCode = jb.getString("parkCode");// 停车场编号
        String deviceCode = jb.getString("deviceCode");// 设备编号
        String directive = jb.getString("directive");// 下发控制指令
        if ("1".equals(directive)) {// 抬杆
            // 更新设备开闸消息
//            EquipSendMsg equipSendMsg = new EquipSendMsg();
//            equipSendMsg.setDevicecode(deviceCode);// 设设备编号
//            equipSendMsg.setMsgType(1);// msg_type 1 开闸
//            equipSendMsg.setInsertTime(new Date());
//            equipSendMsgRepository.save(equipSendMsg);

            // 开闸消息插入缓存
            GateMsg gateMsg = new GateMsg();
            gateMsg.setDevicecode(deviceCode);
            // 写缓存
            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + deviceCode, gateMsg);

            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            JSONObject jb1 = new JSONObject();
            jb1.put("parkCode", parkCode);
            jb1.put("deviceCode", deviceCode);
            jb1.put("feedback", "success");
            responseBodyJa.add(jb1);
            ret.put("responseBody", responseBodyJa);
            log.info("【gateBarrierControl】返回：" + ret.toString());
            return ret.toString();
        } else {// 不支持
            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            JSONObject jb1 = new JSONObject();
            jb1.put("parkCode", parkCode);
            jb1.put("deviceCode", deviceCode);
            jb1.put("feedback", "fail");
            responseBodyJa.add(jb1);
            ret.put("responseBody", responseBodyJa);
            return ret.toString();
        }
    }

    //======================好停车下行对接==================================


    //======================好停车同步对接接口==================================

    /**
     * 离场前会员信息查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/16 15:55
     **/
    @Transactional
    public String queryEparkLeaguer(ParkChargeInfo parkChargeInfo) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");

            JSONObject jb = new JSONObject();
            jb.put("method", "queryPlateInfoBeforeLeave");// 查询会员信息
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("thirdOrderNo", parkChargeInfo.getNid());
            params.put("plateNo", parkChargeInfo.getCarno());
            params.put("plateType", parkChargeInfo.getLicensetype());
            jb.put("params", params);
            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.SYNC_CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【查询Epark会员信息】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==返回信息==" + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 离场前查询会员信息异常==");
            JSONObject jb = new JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * 离场前会员信息查询
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/16 15:55
     **/
    @Transactional
    public String queryEparkLeaguerV19(ParkChargeInfo parkChargeInfo) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");

            JSONObject jb = new JSONObject();
            jb.put("method", "getLeaguerInfo");// 查询会员信息
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("plateNo", parkChargeInfo.getCarno());
            params.put("plateType", parkChargeInfo.getLicensetype());
            jb.put("params", params);
            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.SYNC_CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【查询Epark会员信息】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==返回信息==" + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 离场前查询会员信息异常==");
            JSONObject jb = new JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * Epark会员自动扣费请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/16 16:14
     **/
    @Transactional
    public String orderCharge(ParkChargeInfo parkChargeInfo, double parkingFee) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");

            JSONObject jb = new JSONObject();
            jb.put("method", "orderCharge");// 会员订单扣费
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("orderNo", parkChargeInfo.getNid());
            params.put("chargeFee", parkingFee);
            jb.put("params", params);
            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.SYNC_CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【Epark会员请求扣费】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==返回信息==" + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 离场前会员扣费请求异常==");
            JSONObject jb = new JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * Epark会员自动扣费请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/16 16:14
     **/
    @Transactional
    public String orderChargeV19(ParkChargeInfo parkChargeInfo, double parkingFee, boolean isCard) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");
            String domainId = properties.getProperty("domain_id");
            String domainKey = properties.getProperty("domain_key");
            String openEparkUrl = properties.getProperty("open_epark_url");

            JSONObject jb = new JSONObject();
            jb.put("method", "leaguerCharge");// 会员订单扣费
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("orderNo", parkChargeInfo.getNid());
            params.put("chargeFee", parkingFee);
            if (isCard) {// 参数配置，是否只从余额扣钱
                params.put("isCard", "1");
            } else {
                params.put("isCard", "0");
            }
            jb.put("params", params);
            // 准备参数和签名
            long timeStamp = new Date().getTime();// 获得时间戳
            // 需要签名的数据
            String[] singArr = new String[]{domainId, String.valueOf(timeStamp)};
            // 对数据进行签名
            String sign = SignatureUtil.sign(domainKey, singArr);
            // 请求openEpark
            String url = openEparkUrl + Constant.SYNC_CUSTOMDATA_URL;
            // 请求openEpark
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();//定义body
            body.add("domainId", domainId);
            body.add("timeStamp", String.valueOf(timeStamp));
            body.add("sign", sign);
            body.add("content", jb.toString());
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();
            log.info(DateUtil.formatDateYMDHMS(new Date()) + " ==【Epark会员请求扣费】==车牌号==" + parkChargeInfo.getCarno()
                    + "==orderNo==" + parkChargeInfo.getNid() + "==返回信息==" + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 离场前会员扣费请求异常==");
            JSONObject jb = new JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
            return jb.toString();
        }
    }
    //======================好停车同步对接接口==================================

    //======================自研优惠券下发接口==================================

    /**
     * 接收Epark下发的优惠券功能
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/20 9:14
     **/
    private String receiveNotifyParkOrderCoupon(JSONObject jb) throws Exception {
        JSONArray responseBodyJa = new JSONArray();
        try {
            // String parkCode = jb.getString("parkCode");// 停车场编号
            String plateNo = jb.getString("plateNo");// 车牌号码
            String licensetype = jb.getString("plateType");// 车牌类型
            String couponMoney = jb.getString("couponMoney");// 优惠券金额
            float money = Float.valueOf(couponMoney.trim());
            // 根据车牌号和车牌颜色查询订单信息
            List<ParkChargeInfo> list = parkChargeInfoRepository.findParkChargeInfoUnclosed(plateNo, licensetype);
            if (list.size() <= 0) {// bug修改 <0改为《=0 ，20210511 by sq
                // 返回信息
                JSONObject ret = new JSONObject();
                ret.put("result", "fail");
                ret.put("msg", "没有正在进行的订单");
                ret.put("responseBody", responseBodyJa);
                log.info("【notifyParkOrderCoupon】返回：" + ret.toString());
                return ret.toString();
            }

            // 获取订单信息
            ParkChargeInfo parkChargeInfo = list.get(0);
            float moneyPre = parkChargeInfo.getCouponFee();
            parkChargeInfo.setCouponFee(moneyPre + money);
            // 更新订单信息
            parkChargeInfoRepository.saveAndFlush(parkChargeInfo);

            // 返回信息
            JSONObject ret = new JSONObject();
            ret.put("result", "success");
            ret.put("msg", "success");
            ret.put("responseBody", responseBodyJa);
            log.info("【notifyParkOrderCoupon】返回：" + ret.toString());
            return ret.toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 接收优惠券下发请求异常==");
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
            return jb.toString();
        }
    }

    //======================自研优惠券下发接口==================================
}
