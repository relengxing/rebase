package com.relengxing.rebase.web;


import com.relengxing.rebase.context.PassThroughHolder;
import com.relengxing.rebase.pass.PassThroughLoaded;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * rest 请求拦截器
 * 如果有灰度标识，保存到线程变量，并在请求结束后清理。
 *
 * @author relengxing
 * @date 2023-05-21 11:38
 * @Description
 **/
@Slf4j
public class GrayFlagInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (PassThroughLoaded.passThroughLoaded()) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = request.getHeader(key);
                PassThroughHolder.put(key, value);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求结束后清理线程变量，防止污染
        if (PassThroughLoaded.passThroughLoaded()) {
            PassThroughHolder.clear();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        // 请求结束后清理线程变量，防止污染
        if (PassThroughLoaded.passThroughLoaded()) {
            PassThroughHolder.clear();
        }
    }

}
