package com.lann.openpark;

import com.lann.openpark.camera.dao.entiy.EquipInfo;
import com.lann.openpark.camera.dao.entiy.LedAndVoiceMsg;
import com.lann.openpark.camera.dao.entiy.LedConfig;
import com.lann.openpark.camera.dao.mapper.EquipInfoMapper;
import com.lann.openpark.camera.dao.repository.EquipInfoRepository;
import com.lann.openpark.camera.dao.repository.EquipSendMsgRepository;
import com.lann.openpark.camera.dao.repository.LedConfigRepository;
import com.lann.openpark.camera.dao.repository.VoiceConfigRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.openepark.service.OpenEparkService;
import com.lann.openpark.order.dao.repository.ParkChargeInfoRepository;
import com.lann.openpark.park.service.ParkService;
import com.lann.openpark.system.dao.repository.SysConfigRepository;
import com.lann.openpark.util.EhcacheUtil;
import com.lann.openpark.util.VoiceLedUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// 测试MyBatis和JPA操作
@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    //    @Autowired
    //    ParkService parkService;
//    @Autowired
//    ParkInfoMapper parkInfoMapper;
    @Autowired
    EquipSendMsgRepository equipSendMsgRepository;

    @Autowired
    ParkChargeInfoRepository parkChargeInfoRepository;

    @Autowired
    LedConfigRepository ledConfigRepository;
    @Autowired
    VoiceConfigRepository voiceConfigRepository;

    @Autowired
    OpenEparkService openEparkService;

    @Autowired
    EquipInfoMapper equipInfoMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    SysConfigRepository sysConfigRepository;

    @Autowired
    ParkService parkService;

    @Autowired
    EquipInfoRepository equipInfoRepository;


    @Test
    public void testRepository() throws Exception {

//        EquipSendMsg equipSendMsg = new EquipSendMsg();// 生成下发消息
//        equipSendMsg.setDevicecode("1111");// 离场设设备编号
//        equipSendMsg.setMsgType(1);// msg_type 1 开闸
//        equipSendMsg.setInsertTime(new Date());
//        equipSendMsgRepository.save(equipSendMsg);


        // 测试语音播报和LED显示
//        for (int i = 0; i < 1; i++) {
//            Map map = VoiceLedUtil.getVoiceStr("鲁LS9838，已缴费 4.5元。祝您一路顺风。", (byte) 1);
//            int length = (int) map.get("length");
//            String baseStr = (String) map.get("baseStr");
//            EquipSendMsg equipSendMsg = new EquipSendMsg();
//            equipSendMsg.setDevicecode("12c0ac2f-b4849bc6");
//            equipSendMsg.setMsgType(3);
//            equipSendMsg.setChannel(1);
//            equipSendMsg.setContent(baseStr);
//            equipSendMsg.setDataLength(length);
//            equipSendMsgRepository.save(equipSendMsg);
//            Thread.sleep(1000);
//        }

        Map map = VoiceLedUtil.getLedText((byte) 2, "安全为天平安是福", (byte) 7, (byte) 0, (byte) 10, 255, (byte) 0);
        int length = (int) map.get("length");
        String baseStr = (String) map.get("baseStr");
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        LedAndVoiceMsg ledAndVoiceMsg = new LedAndVoiceMsg();
        ledAndVoiceMsg.setDataLength(length);
        ledAndVoiceMsg.setContent(baseStr);
        List<EquipInfo> equipList = equipInfoRepository.findAllById(Arrays.asList("Iterable"));
        EquipInfo equipInfo = equipList.get(0);
        ledAndVoiceMsg.setChannel(equipInfo.getVoiceChannel());
        ledAndVoiceMsg.setDevicecode(equipInfo.getDevicecode());
        ehcacheUtil.put(Constant.PARK_CACHE, "update_time_" + equipInfo.getDevicecode(), ledAndVoiceMsg);

//        EquipSendMsg equipSendMsg = new EquipSendMsg();
//        equipSendMsg.setDevicecode("12c0ac2f-b4849bc6");
//        equipSendMsg.setMsgType(3);
//        equipSendMsg.setChannel(1);
//        equipSendMsg.setContent(baseStr);
//        equipSendMsg.setDataLength(length);
//        equipSendMsgRepository.save(equipSendMsg);

//        测试epark同步接口
//        ParkChargeInfo parkChargeInfo = parkChargeInfoRepository.findParkChargeInfoByNid("40281f8173998388017399865d450005");
//        String retStr = openEparkService.queryEparkLeaguer(parkChargeInfo);
//        JSONObject jb_leaguer = JSONObject.fromObject(retStr);
//        if (jb_leaguer.getString("result").equals("success")) {
//            jb_leaguer = jb_leaguer.getJSONObject("responseBody");
//            System.out.println("解析商户代扣:" + jb_leaguer.getJSONObject("plateInfo").getBoolean("merchantPayFor"));
//            JSONObject jb1 = jb_leaguer.getJSONObject("leaguerInfo");
//            if (jb1.size() > 0) {// 说明是会员
//                String autoPay = jb1.getString("autoPay");
//                String etcSwitch = jb1.getString("etcSwitch");
//                String leaguerId = jb1.getString("leaguerId");
//                String phoneNo = jb1.getString("phoneNo");
//                System.out.println("自动扣费：" + autoPay + " etc开关：" + etcSwitch + " leaguerId：" + leaguerId + " phoneNo：" + phoneNo);
//            } else {
//                System.out.println("该用户非会员");
//            }
//        }

//        List<EquipGateInfo> list_equip = equipInfoMapper.findEquipAndGateInfo("12c0ac2f-b4849bc6");
//        EquipGateInfo equipGateInfo = list_equip.get(0);
//        VoiceLedThread thread = new VoiceLedThread(equipSendMsgRepository, ledConfigRepository, voiceConfigRepository, "鲁L12345", equipGateInfo.getDevicecode(), equipGateInfo.getVoiceChannel(), 1, 5, false, 1000, 0);
//        thread.run();

//        int voiceType = 1;
//        int ledType = 5;
//        String plateNo = "鲁L12345";
//        boolean isVip = false;
//        Thread.sleep(1000);// 线程休眠
//        String voiceMsg = "";
//        // 根据语音播报类型查询语音模板
//        VoiceConfig voiceConfig = voiceConfigRepository.findVoiceConfigByType(voiceType);
//        List<LedConfig> list = ledConfigRepository.findLedConfigByType(ledType);
//        if (1 == voiceType || 2 == voiceType) {// 驶入
//            voiceMsg = voiceConfig.getTemplate();
//            // 替换文本
//            voiceMsg = voiceMsg.replace("${plateNo}", plateNo);
//        }
//        List<LedConfig> list1 = new ArrayList<>();
//        if (1 == ledType || 2 == ledType || 4 == ledType) {// 支付
//
//        } else if (3 == ledType) {// 扫码支付成功
//
//        } else if (5 == ledType) {// 驶入
//            // 第三行第四行是需要替换
//            if (list.size() == 4) {
//                list1.add(this.getLineBynum(list, 0));
//
//                LedConfig ledConfig1 = this.getLineBynum(list, 1);
//                String text = ledConfig1.getText();
//                text = text.replace("${plateNo}", plateNo);
//                ledConfig1.setText(text);
//                list1.add(ledConfig1);
//                LedConfig ledConfig2 = this.getLineBynum(list, 2);
//                // 获取内容替换
//                text = ledConfig2.getText();
//                if (isVip) {
//                    text = text.replace("${isVip}", "固定车");
//                } else {
//                    text = text.replace("${isVip}", "临时车");
//                }
//                ledConfig2.setText(text);
//                list1.add(ledConfig2);
//                list1.add(this.getLineBynum(list, 3));
//
//            }
//        } else if (6 == ledType) {// 永久广告语
//
//        }
//
//        Map map = VoiceLedUtil.getVoicrAndLedStr(list1, voiceMsg, 0);
//        if ((int) map.get("length") > 0) {
//            int length = (int) map.get("length");
//            String baseStr = (String) map.get("baseStr");
//            EquipSendMsg equipSendMsg = new EquipSendMsg();
//            equipSendMsg.setDevicecode(equipGateInfo.getDevicecode());
//            equipSendMsg.setMsgType(Constant.SEND_MSG_TYPE);
//            equipSendMsg.setChannel(equipGateInfo.getVoiceChannel());
//            equipSendMsg.setContent(baseStr);
//            equipSendMsg.setDataLength(length);
//            equipSendMsgRepository.save(equipSendMsg);
//        }


//        String url = "http://127.0.0.1:9020/openEpark/";
//        try {
//            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(null, null);
//            ResponseEntity<String> retbody = restTemplate.postForEntity(url, httpEntity, String.class);
//            System.out.println(retbody.getStatusCode());
//            if (200 == retbody.getStatusCodeValue()) {
//                System.out.println("请求成功！");
//            } else {
//                System.out.println("请求失败1！");
//            }
//        } catch (Exception e) {
//            System.out.println("请求失败！");
//        }

//        List<SysConfig> list = sysConfigRepository.findSysConfigAll();
//        Map<String, String> collect = list.stream().collect(Collectors.toMap(SysConfig::getConfig_key, SysConfig::getConfig_value));
//        System.out.println(collect);
//        // 将转换后的列表加入属性中
//        Properties properties = new Properties();
//        properties.putAll(collect);
//        System.out.println(properties);
    }


    private LedConfig getLineBynum(List<LedConfig> list, int lineNum) {
        LedConfig ledConfig1 = null;
        for (LedConfig ledConfig : list) {
            if (ledConfig.getLineId() == lineNum) {
                ledConfig1 = ledConfig;
                break;
            }
        }
        return ledConfig1;
    }


    // @Test
    public void testPark() throws InterruptedException {

//        // 模拟车辆进程
//        for (int i = 0; i < 100; i++) {
//            Thread thread = new Thread((Runnable) new ParkThread(parkService, 9, 0));
//            thread.start();
//        }
//
//        // 模拟车辆出场
//        for (int i = 0; i < 77; i++) {
//            Thread thread = new Thread((Runnable) new ParkThread(parkService, 10, 0));
//            thread.start();
//        }

        Thread.sleep(1000 * 60 * 60);
    }


    @Test
    public void test1() throws Exception {

        URI uri = UriComponentsBuilder.fromUriString("http://localhsot:9000/openpark?name={name}").build().expand("dodo").toUri();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
    }


}
