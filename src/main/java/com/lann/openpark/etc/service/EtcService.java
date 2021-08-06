package com.lann.openpark.etc.service;

import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.bean.EtcBean;
import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.entiy.GateMsg;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.repository.CameraDataRepository;
import com.lann.openpark.camera.dao.repository.EquipSendMsgRepository;
import com.lann.openpark.camera.dao.repository.LedConfigRepository;
import com.lann.openpark.camera.dao.repository.VoiceConfigRepository;
import com.lann.openpark.camera.threads.UpLoadDriveOutThread;
import com.lann.openpark.camera.threads.UpLoadPayDetailThread;
import com.lann.openpark.camera.threads.VoiceLedThread;
import com.lann.openpark.charge.bizobj.ParkingInfo;
import com.lann.openpark.common.Constant;
import com.lann.openpark.openepark.dao.entiy.OrderPay;
import com.lann.openpark.openepark.dao.repository.OrderPayRepository;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.websocket.WebSocketServer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 停车场域openEpark对接类
 *
 * @Author songqiang
 * @Description
 * @Date 2020/7/10 15:11
 **/
@Service
@Transactional
public class EtcService {

    private static final Logger log = LoggerFactory.getLogger(EtcService.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    OrderPayRepository orderPayRepository;
    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;
    @Autowired
    OpenEparkService openEparkService;
    @Autowired
    EquipSendMsgRepository equipSendMsgRepository;
    @Autowired
    LedConfigRepository ledConfigRepository;
    @Autowired
    VoiceConfigRepository voiceConfigRepository;
    @Autowired
    EquipInfoMapper equipInfoMapper;
    @Autowired
    CameraDataRepository cameraDataRepository;

    @Value("${parkInfo.isVoice}")
    private boolean isVoice;//

    public static void main(String[] args) {

        // DateTime date = new DateTime(new Date());

//        new Thread(new Runnable() {
//            public void run() {
//                for (int i = 0; i < 10; i++) {
//                    System.out.println(i);
//                }
//            }
//        }).start();


//        JSONObject jbPay = jbRes.getJSONObject("data");
//        String parkCode = jbPay.getString("parkCode");
//        String outTradeNo = jbPay.getString("outTradeNo");
//        Double derateFee = jbPay.getDouble("derateFee");
//        Double payFee = jbPay.getDouble("payFee");
//        String payTime = jbPay.getString("payTime");

//        JSONObject jb = new JSONObject();
//        jb.put("flag", true);
//
//        JSONObject jbData = new JSONObject();
//        jbData.put("parkCode", "371104");
//        jbData.put("outTradeNo", "40281f8174c2ec8e0174c300cb170015");
//        jbData.put("derateFee", 0);
//        jbData.put("payFee", 2.0);
//        jbData.put("payTime", "2020-09-25 10:20:00");
//        jb.put("data", jbData);

//        JSONObject ret = new JSONObject();
//        ret.put("result", "success");
//        ret.put("msg", "success");
//        ret.put("responseBody", new JSONArray());

        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", new JSONArray());

        System.out.println(ret.toString());


    }

    /**
     * ETC扣费请求
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/4 17:34
     **/
    public String etcCharge(EquipGateInfo equipGateInfo, ParkingInfo parkingInfo,
                            double parkingFee, ParkChargeInfo parkChargeInfo, CameraData cameraData, EtcBean etcBean,
                            boolean isVoice) {
        try {

            EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();// 获取缓存读取工具
            Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
            String parkCode = properties.getProperty("park_code");

            JSONObject jb = new JSONObject();
            String lane_num = equipGateInfo.getPointcode();// 车道号
            String trans_order_no = parkChargeInfo.getNid();// 交易订单号
            String park_code = parkCode;// 车场编号
            String plate_no = parkChargeInfo.getCarno();// 车牌号
            int colorType = cameraData.getColorType();
            // 车牌颜色和车辆类型
            String plate_color_code = Constant.convertColorType4Etc(colorType);// 车牌颜色
            String plate_type_code = Constant.convertVehicleType(parkChargeInfo.getVehicleType());// 车辆类型编码 0:小车 1:大车 2:超大车
            DateTime timeIn = parkingInfo.getDriveIn();
            int entrance_time = (int) (timeIn.getMillis() / 1000);// 入场时间
            int park_record_time = parkingInfo.getParkLength() * 60;// 停车时长
            DateTime timeOut = parkingInfo.getDriveOut();
            int exit_time = (int) (timeOut.getMillis() / 1000);// 离场时间
            double deduct_amount = parkingFee;// 停车费用
            double receivable_total_amount = parkingFee;// 应收金额
            double discount_amount = 0;// 折扣金额

            jb.put("lane_num", lane_num);
            jb.put("trans_order_no", trans_order_no);
            jb.put("park_code", park_code);
            jb.put("plate_no", plate_no);
            jb.put("plate_color_code", plate_color_code);
            jb.put("plate_type_code", plate_type_code);
            jb.put("entrance_time", entrance_time);
            jb.put("park_record_time", park_record_time);
            jb.put("exit_time", exit_time);
            jb.put("deduct_amount", deduct_amount);
            jb.put("receivable_total_amount", receivable_total_amount);
            jb.put("discount_amount", discount_amount);

            log.info("ETC扣费请求数据：" + jb.toString());

            // 请求ETC扣费
            String url = etcBean.getEtcUrl();
            LinkedMultiValueMap<String, String> header = new LinkedMultiValueMap<>();
            header.add("Content-Type", "application/json;charset=utf-8");
            HttpEntity<String> httpEntity = new HttpEntity<>(jb.toString(), header);
            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
            String jsonString = retbody.getBody();

            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("===" + DateUtil.formatDateYMDHMS(new Date()) + " === ETC扣费请求异常==");
            JSONObject jb = new JSONObject();
            jb.put("flag", false);
            jb.put("msg", "Exception：" + e.getMessage());
            return jb.toString();
        }
    }

    /**
     * 接收ETC支付返回结果
     *
     * @Author songqiang
     * @Description
     * @Date 2020/9/23 18:02
     **/
    public String etcPayDetail(String bodyString) throws Exception {
        JSONObject jbRes = JSONObject.fromObject(bodyString);
        log.info("接收ETC扣费成功下发：" + bodyString);
        // 解析返回结果
        if (jbRes.getBoolean("flag")) {// 扣费成功
            JSONObject jbPay = jbRes.getJSONObject("data");
            String parkCode = jbPay.getString("parkCode");
            String outTradeNo = jbPay.getString("outTradeNo");
            Double derateFee = jbPay.getDouble("derateFee");
            Double payFee = jbPay.getDouble("payFee");
            String payTime = jbPay.getString("payTime");

            // 根据订单号，查询订单信息
            ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid(outTradeNo);
            if (null == parkChargeInfo) {// 没有查询到订单信息，直接返回成功
                JSONObject ret = new JSONObject();
                ret.put("result", "fail");
                ret.put("msg", "未查询到订单信息");
                ret.put("responseBody", new JSONArray());
                log.info("【etcPayDetail】返回：" + ret.toString());
                return ret.toString();
            }

            // 判断订单状态
            if (!("0".equals(parkChargeInfo.getExitType()) || Constant.EXIT_TYPE_TMP.equals(parkChargeInfo.getExitType()))) {
                JSONObject ret = new JSONObject();
                ret.put("result", "fail");
                ret.put("msg", "订单已结束");
                ret.put("responseBody", new JSONArray());
                log.info("【etcPayDetail】返回：" + ret.toString());
                return ret.toString();
            }

            // 1.插入支付记录
            OrderPay orderPay = new OrderPay();
            orderPay.setParkCode(parkCode);
            orderPay.setOrderNo(parkChargeInfo.getNid());
            orderPay.setCouponFee(0.0f);
            orderPay.setDerateFee(derateFee.floatValue());
            orderPay.setOutTradeNo(outTradeNo);
            orderPay.setPayFee(payFee.floatValue());
            orderPay.setPayType(Constant.PAY_TYPE_ETC);// ETC支付
            if (StringUtils.isNotEmpty(payTime)) {// 支付时间
                orderPay.setPayTime(DateUtil.toDateTime(payTime));
            }
            orderPay.setInsertTime(new Date());
            orderPayRepository.save(orderPay);

            // 2.更新订单信息
            float charge = parkChargeInfo.getCharge() + payFee.floatValue();
            parkChargeInfo.setCharge(charge);// 更新实交费用
            parkChargeInfo.setPayType(Constant.PAY_TYPE_ETC);
            float derate_fee = parkChargeInfo.getDerateFee() + derateFee.floatValue();
            parkChargeInfo.setDerateFee(derate_fee);
            parkChargeInfo.setPayUpload(0);// 支付记录可以上传
            if (StringUtils.isNotEmpty(payTime)) {// 支付时间
                parkChargeInfo.setPayTime(DateUtil.toDateTime(payTime));
            }

            // 判断是否插入开闸消息
            if (parkChargeInfo.getCharge() >= parkChargeInfo.getTotalcharge()) {

                // 获取缓存读取工具
                EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
                // 读取ETC配置
                Properties properties = (Properties) ehcacheUtil.get(Constant.CONFIG_CACHE, "sys_properties");
                // 桌面推送
                String isDesk = properties.getProperty("is_desk");
                if ("1".equals(isDesk)) {
                    WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_PAY_MESSAGE, Constant.getPayTypeName("1229"), String.valueOf(parkChargeInfo.getCharge()));
                }

                parkChargeInfo.setExitType(Constant.EXIT_TYPE_AUTO);// 修改欠费驶离为自动放行
                parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);

                List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo(parkChargeInfo.getDevicecodeExt());

                if (list_equip.size() > 0) {
                    EquipGateInfo equipGateInfo = list_equip.get(0);
                    // 开闸
                    GateMsg gateMsg = new GateMsg();
                    gateMsg.setDevicecode(equipGateInfo.getDevicecode());
                    // 写缓存
                    ehcacheUtil.put(Constant.PARK_CACHE, "gate_" + equipGateInfo.getDevicecode(), gateMsg);

                    // 是否下发语音
                    if (isVoice) {
                        // 下发语音和LED屏显消息
                        // voiceType = 3
                        // ledType = 1
                        VoiceLedThread thread = new VoiceLedThread(parkChargeInfo.getCarno(), equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 3, 1, null, 1000, charge);
                        thread.run();
                    }
                }

                CameraData cameraData = cameraDataRepository.findCameraDataByNid(parkChargeInfo.getExitNid());
                // 上传支付
                Thread upLoadPayDetailThread = new Thread((Runnable) new UpLoadPayDetailThread(parkChargeInfo, orderPay, cameraData));
                upLoadPayDetailThread.start();
                // 上传驶离
                Thread upLoadDriveOutThread = new Thread((Runnable) new UpLoadDriveOutThread(parkChargeInfo, cameraData, "8102"));
                upLoadDriveOutThread.start();

            } else {
                parkChargeInfo = parkChargeInfoRepository.saveAndFlush(parkChargeInfo);
            }
        } else {// 扣费失败
            log.info("ETC下发扣费失败消息，不予处理");
            JSONObject ret = new JSONObject();
            ret.put("result", "fail");
            ret.put("msg", "success");
            ret.put("responseBody", new JSONArray());
            log.info("【etcPayDetail】返回：" + ret.toString());
            return ret.toString();
        }
        JSONObject ret = new JSONObject();
        ret.put("result", "success");
        ret.put("msg", "success");
        ret.put("responseBody", new JSONArray());
        log.info("【etcPayDetail】返回：" + ret.toString());
        return ret.toString();
    }
}
