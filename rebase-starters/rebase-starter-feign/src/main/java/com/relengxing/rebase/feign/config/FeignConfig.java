package com.relengxing.rebase.feign.config;

import com.relengxing.rebase.feign.interceptor.FeignPassRequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chaoli
 * @date 2023-10-19 22:12
 * @Description
 **/
@Configuration
public class FeignConfig {

    @Bean
    @ConditionalOnMissingBean
    public FeignPassRequestInterceptor feignPassRequestInterceptor() {
        return new FeignPassRequestInterceptor();
    }

}
