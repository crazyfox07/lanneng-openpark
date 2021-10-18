package com.lann.openpark.interceptor;

import com.lann.openpark.common.Constant;
import com.lann.openpark.util.EhcacheUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * desktop客户端登录拦截器
 *
 * @Author songqiang
 * @Description
 * @Date 2020/10/29 13:56
 **/
@Component
@Slf4j
public class ClientInterceptor extends HandlerInterceptorAdapter {

    // 白名单请求
    public static List whiteList = Arrays.asList(
            "/api/client/login",// 登录
            // 导出
            "/api/client/exportVipList",
            "/api/client/exportOrderList",
            "/api/client/exportOrderPayList",
            "/api/client/exportCarsdelList",
            "/api/client/exportManaulPoleList",
            "/api/client/exportFreeGoList",
            "/api/client/exportCarnoList",
            "/api/client/exportDetectList",
            "/api/client/updateSysConfig");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        EhcacheUtil ehcacheUtil = EhcacheUtil.getInstance();
        // 获取客户端url
        String url = request.getRequestURI();
//        url = URLDecoder.decode(url);
//        log.info("请求地址: " + url);
//        String urlSub = "";
//        if (url.indexOf("?") > 0) {
//            urlSub = url.substring(0, url.indexOf("?"));
//            log.info("截取后的请求地址: " + urlSub);
//        } else {
//            urlSub = url;
//        }
        if (whiteList.contains(url)) {// 不拦截的请求
            return true;
        } else {
            // 获取token
            String token = request.getHeader("token");
            // log.info("token: " + token);
            // 缓存读取token
            String token_cache = (String) ehcacheUtil.get(Constant.PARK_CLIENT, "client_cookie_" + token);
            // 验证拦截、放行
            if (StringUtils.isEmpty(token_cache) || !(token.equals(token_cache))) {
                JSONObject jb = new JSONObject();
                jb.put("code", "101");
                jb.put("message", "登录超时，请重新登录");
                returnJson(response, jb.toString());
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * 拦截成功返回json格式的错误信息
     *
     * @Author songqiang
     * @Description
     * @Date 2020/10/29 13:58
     **/
    private void returnJson(HttpServletResponse response, String json) throws Exception {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

}