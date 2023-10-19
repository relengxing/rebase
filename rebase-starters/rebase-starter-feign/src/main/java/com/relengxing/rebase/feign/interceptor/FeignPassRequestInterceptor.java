package com.relengxing.rebase.feign.interceptor;

import com.relengxing.rebase.context.PassThroughHolder;
import com.relengxing.rebase.pass.PassThroughLoaded;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.Map;

/**
 * 透传
 */
public class FeignPassRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (PassThroughLoaded.passThroughLoaded()) {
            for (Map.Entry<String, String> entry : PassThroughHolder.getAll().entrySet()) {
                requestTemplate.header(entry.getKey(), entry.getValue());
            }
        }
    }
}
