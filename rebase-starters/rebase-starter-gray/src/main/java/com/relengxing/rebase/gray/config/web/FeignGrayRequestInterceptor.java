package com.relengxing.rebase.gray.config.web;

import com.relengxing.rebase.constant.BaseConstant;
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
            requestTemplate.header(BaseConstant.GRAY_HEADER, grayFlag);
        }
        if (traceFlag != null) {
            requestTemplate.header(BaseConstant.TRACE_HEADER, traceFlag);
        }
    }
}
