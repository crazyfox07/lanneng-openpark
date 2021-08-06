package com.lann.openpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableScheduling// 开启任务调度
@SpringBootApplication
@ComponentScan(basePackages = {"com.lann"})// 扫面纳入spring管理的组件
@EnableSwagger2// 开启swagger接口测试、文档工具
@EnableCaching// 开启springboot缓存支持
public class OpenparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenparkApplication.class, args);
    }

    /**
     * restTemplate http请求，OkHttp3实现
     *
     * @return
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        return restTemplate;
    }


}
