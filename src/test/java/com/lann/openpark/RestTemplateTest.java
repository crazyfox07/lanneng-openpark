package com.lann.openpark;

import com.alibaba.fastjson.JSONObject;
import com.lann.openpark.common.Constant;
import com.lann.openpark.util.DateUtil;
import com.lann.openpark.util.SignatureUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

/**
 * @author https://www.jianshu.com/p/8cc05da7a6a2(简书参考地址)
 * @version 1.0
 * @create 2018-09-12 18:11
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class RestTemplateTest {

    @Autowired
    RestTemplate restTemplate;

    private static String baseUrl = "http://127.0.0.1:8888/api/private/v1/";

//    @Autowired
//    StringEncryptor encryptor;
//
//    @Test
//    public void getEncryptor() {
//        //对敏感信息进行加密
//        String name = encryptor.encrypt("root");
//        String password = encryptor.encrypt("666666");
//        System.out.println(name + "----------------");
//        System.out.println(password + "----------------");
//    }

    // @Test

    /**
     * get请求
     */
    public void testRestTemplate() {

        // 简单调用 以下是controller配置
        // @RequestMapping("/cms/config")
        // @GetMapping("/getmodel/{id}")
        // 调用
        ResponseEntity<Map> forEntity =
                restTemplate.getForEntity("http://localhost:31001/cms/config/getmodel/5a791725dd573c3574ee333f", Map.class);
        Map body = forEntity.getBody();
        System.out.println(body);
    }

    /**
     * post请求
     */
    // @Test
    public void testRestTemplate1() {

        String url = baseUrl + "login";

        JSONObject postData = new JSONObject();
        postData.put("username", "admin");
        postData.put("password", "123456");

        JSONObject json = restTemplate.postForEntity(url, postData, JSONObject.class).getBody();
        System.out.println(json);

        // 令牌
        // Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1aWQiOjUwMCwicmlkIjowLCJpYXQiOjE1ODI1MjI5MTcsImV4cCI6MTU4MjYwOTMxN30.8Mrrek8FcuCKPd8dHEYvQvs05iOrBxoX6jJJgUqRnUg

    }

    /**
     * 其他请求，header或者body中携带参数的请求
     * 一般不会使用
     */
    @Test
    public void testRestTemplate2() {
        try {

            String parkCode = "371104";
//            String domainId = "rizhaotc514ab828009cb44260350004";
//            String domainKey = "2c8bb5813d3928fb21527d9f1c94c7b7";
//            String openEparkUrl = "http://127.0.0.1:9020/openEpark/";

            String domainId = "rizhaotc514ab828009cb44260350004";
            String domainKey = "2c8bb5813d3928fb21527d9f1c94c7b7";
            String openEparkUrl = "http://jtss.rzbus.cn/openEpark/";

            net.sf.json.JSONObject jb = new net.sf.json.JSONObject();
            jb.put("method", "getLeaguerInfo");// 查询会员信息
            net.sf.json.JSONObject params = new net.sf.json.JSONObject();
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
            net.sf.json.JSONObject jb = new net.sf.json.JSONObject();
            jb.put("result", "fail");
            jb.put("msg", "Exception：" + e.getMessage());
        }
    }


}
