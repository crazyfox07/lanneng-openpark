package com.lann.openpark.charge.service;

import ccb.pay.api.util.CCBPayUtil;
import com.lann.openpark.camera.bean.EquipGateInfo;
import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.entiy.GateMsg;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.repository.CameraDataRepository;
import com.lann.openpark.camera.threads.UpLoadDriveOutThread;
import com.lann.openpark.camera.threads.UpLoadPayDetailThread;
import com.lann.openpark.common.Constant;
import com.lann.openpark.common.response.ResponseResult;
import com.lann.openpark.openepark.dao.entiy.OrderPay;
import com.lann.openpark.openepark.dao.repository.OrderPayRepository;
import com.lann.openpark.order.dao.entiy.ParkChargeInfo;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.websocket.WebSocketServer;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class CcbWuGanService {
    private Logger log = LoggerFactory.getLogger(CcbWuGanService.class);
    private final String MERCHANTID = "105000075235505";
    private final String POSID = "056339629";
    private final String BRANCHID = "370000000";
    // 获取柜台完整公钥
    private final String pubKey = "30819d300d06092a864886f70d010101050003818b00308187028181009b5a97affd892036eddb0ad57122967e5850e1e6ff65f91dbf09e45a3f477595b0333bc0f0abe0fe616048785c46ba5c9d3f59c6fd63f4f3ffd3bb61fa4b6354c88df178cc61440963663c9d29edc88d7850d560ff89c3e5bd7208546d6adca2ea70be44c6948955242aa2eb554edf669bccc8d7a21c58b44f2fb56fb04f8763020111";

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;
    @Autowired
    OrderPayRepository orderPayRepository;
    @Autowired
    EquipInfoMapper equipInfoMapper;
    @Autowired
    CameraDataRepository cameraDataRepository;

    /**
     * 无感支付预查询
     *
     * @throws Exception
     */
    public JSONObject wgzfycx(JSONObject jsonObject) throws Exception {
        try {
            // 解析json
            String carNo = jsonObject.getString("carNo");
            // 建行无感支付预查询接口
            String host = "https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_00_BEPAY?";
            // 商户信息
            String merInfo = "MERCHANTID=" + MERCHANTID + "&POSID=" + POSID + "&BRANCHID=" + BRANCHID;
            //加密原串【PAY100接口定义的请求参数】
            String param = merInfo + "&GROUPMCH=&TXCODE=CXY001&MERFLAG=1&TERMNO1=&TERMNO2=&AUTHNO=" + carNo + "&PlateColor=&Crdt_No=";
            //执行加密操作
            CCBPayUtil ccbPayUtil = new CCBPayUtil();
            String url = ccbPayUtil.makeCCBParam(param, pubKey);
            //拼接请求串
            url = host + merInfo + "&ccbParam=" + url;
            System.out.println(url);
            String resStr = restTemplate.getForObject(url, String.class);
            JSONObject resJson = JSONObject.fromObject(resStr);
            return resJson;
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }

    /**
     * 无感支付扣款
     *
     * @throws Exception
     */
    public JSONObject wgzfkk(JSONObject jsonObject) throws Exception {
        try {
            // 解析json
            String ORDERID = jsonObject.getString("ORDERID");
            String carNo = jsonObject.getString("carNo");
            String AMOUNT = jsonObject.getString("AMOUNT");
            //银行接口url
            String host = "https://ibsbjstar.ccb.com.cn/CCBIS/B2CMainPlat_02_BEPAY?";
            String merInfo = "MERCHANTID=" + MERCHANTID + "&POSID=" + POSID + "&BRANCHID=" + BRANCHID;
            //加密原串【PAY100接口定义的请求参数】
            String param = merInfo + "&GROUPMCH=&TXCODE=CXY000&MERFLAG=1&TERMNO1=&TERMNO2=&ORDERID=" + ORDERID +
                    "&AUTHNO=" + carNo + "&AMOUNT=" + AMOUNT + "&RFNDTPCD=&BSNITMDSC=&TXNITMDSC=&PROINFO=&PlateColor=&REMARK1=&REMARK2=";
            //执行加密操作
            System.out.println(param);
            CCBPayUtil ccbPayUtil = new CCBPayUtil();
            String url = ccbPayUtil.makeCCBParam(param, pubKey);
            //拼接请求串
            url = host + merInfo + "&ccbParam=" + url;
            System.out.println(url);
            String resStr = restTemplate.postForObject(url, null, String.class);
            System.out.println(resStr);
            JSONObject resJson = JSONObject.fromObject(resStr);
            return resJson;
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }

    }

    /**
     * 建行无感支付详情
     *
     * @param jbPay
     * @return
     * @throws Exception
     */
    public String ccbWuganPayDetail(JSONObject jbPay) throws Exception {
        try {
            // 解析返回结果
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
            orderPay.setPayType(Constant.PAY_TYPE_CCB_WUGAN);// 建行无感支付
            if (StringUtils.isNotEmpty(payTime)) {// 支付时间
                orderPay.setPayTime(DateUtil.toDateTime(payTime));
            }
            orderPay.setInsertTime(new Date());
            orderPayRepository.save(orderPay);

            // 2.更新订单信息
            float charge = parkChargeInfo.getCharge() + payFee.floatValue();
            parkChargeInfo.setCharge(charge);// 更新实交费用
            parkChargeInfo.setPayType(Constant.PAY_TYPE_CCB_WUGAN);  // 建行无感支付
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
                    WebSocketServer.sendDeskMessage(parkChargeInfo, Constant.SEND_PAY_MESSAGE, Constant.getPayTypeName("1230"), String.valueOf(parkChargeInfo.getCharge()));
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

            return ResponseResult.SUCCESS().toString();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.toString());
            return ResponseResult.FAIL().toString();
        }
    }
}
