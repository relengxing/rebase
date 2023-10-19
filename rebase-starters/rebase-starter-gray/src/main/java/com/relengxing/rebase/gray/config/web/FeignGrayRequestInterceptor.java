package com.relengxing.rebase.gray.config.web;

import com.relengxing.rebase.constant.GrayConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import com.relengxing.rebase.gray.context.TraceContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignGrayRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        String grayFlag = GrayContext.get();
        String traceFlag = TraceContext.get();
        if (grayFlag != null) {
            requestTemplate.header(GrayConstant.GRAY_HEADER, grayFlag);
        }
        if (traceFlag != null) {
            requestTemplate.header(GrayConstant.TRACE_HEADER, traceFlag);
        }
    }
}
