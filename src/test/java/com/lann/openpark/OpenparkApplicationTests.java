package com.lann.openpark;

import com.lann.openpark.camera.dao.entiy.CameraData;
import com.lann.openpark.camera.dao.repository.CameraDataRepository;
import com.lann.openpark.common.Constant;
import com.lann.openpark.park.service.ParkService;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.SignatureUtil;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class OpenparkApplicationTests {


    @Autowired
    CameraDataRepository cameraDataRepository;
    @Autowired
    ParkService parkService;
    @Autowired
    RestTemplate restTemplate;

    public OpenparkApplicationTests() {

    }

    /**
     * 测试图片上传
     *
     * @Author songqiang
     * @Description
     * @Date 2020/7/3 16:31
     **/
    // @Test
    public void testImgUpload() {

//        System.out.println(fileRoot);
//        System.out.println(suffix);


        CameraData cameraData = cameraDataRepository.getOne("40281f8172a0d2c30172a0e005dd0008");
        String imgBase64Str = Base64Utils.encodeToString(cameraData.getImageFile());
        String[] images = new String[1];
        images[0] = imgBase64Str;

//        DateUtil.toDate("yyyyMMdd");
//        String fileRoot = "C:\\apache-tomcat-7.0.96\\webapps\\FileUpload\\";
//        String suffix = ".jpg";

//        List<String> imageNames = FileOperateUtil.saveThirdParkBase64Images(fileRoot, images, suffix);
//        System.out.println(imageNames.get(0));


    }

    @Test
    public void testPark() {
        try {

            String parkCode = "371104";
            String domainId = "rizhaotc514ab828009cb44260350004";
            String domainKey = "2c8bb5813d3928fb21527d9f1c94c7b7";
            String openEparkUrl = "http://127.0.0.1:9020/openEpark/";

            JSONObject jb = new JSONObject();
            jb.put("method", "getLeaguerInfo");// 查询会员信息
            JSONObject params = new JSONObject();
            params.put("parkCode", parkCode);
            params.put("plateNo", "鲁LS9838");
            params.put("plateType", "2");
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
            System.out.println(DateUtil.formatDateYMDHMS(new Date()) + " ==【查询Epark会员信息】==车牌号== 鲁LS9838，" + "==返回信息==" + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("===" + DateUtil.formatDateYMDHMS(new Date()) + " === 离场前查询会员信息异常==");
            JSONObject jb = new JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
        }
    }


}
