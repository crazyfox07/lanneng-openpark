package com.lann.openpark.config;

import com.lann.openpark.interceptor.ClientInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {
    @Autowired
    private ClientInterceptor clientInterceptor;

    /**
     * 注册拦截器
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 13:55
     **/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // desktop客户端登录信息验证拦截器
        registry.addInterceptor(clientInterceptor).addPathPatterns("/api/client/*");
        registry.addInterceptor(clientInterceptor).addPathPatterns("/api/client1/*");
    }

}