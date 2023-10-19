package com.relengxing.rebase.gray.config.web;


import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import com.relengxing.rebase.gray.context.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        // 判断请求是否来自灰度
        String grayFlag = request.getHeader(BaseConstant.GRAY_HEADER);
        if (grayFlag != null) {
            GrayContext.set(grayFlag);
        }
        String traceFlag = request.getHeader(BaseConstant.TRACE_HEADER);
        if (traceFlag != null) {
            TraceContext.set(traceFlag);
            log.info("TraceContext web trace: {}", traceFlag);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求结束后清理线程变量，防止污染
        GrayContext.clear();
        TraceContext.clear();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        // 请求结束后清理线程变量，防止污染
        GrayContext.clear();
        TraceContext.clear();
    }

}
