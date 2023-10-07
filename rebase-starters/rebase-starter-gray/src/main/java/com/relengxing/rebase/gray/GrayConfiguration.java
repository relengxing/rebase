package com.relengxing.rebase.gray;

import com.relengxing.rebase.gray.config.web.FeignGrayRequestInterceptor;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.gray.configserver.apollo.ApolloListener;
import com.relengxing.rebase.gray.configserver.nacos.NacosListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author relengxing
 * @date 2023-10-07 20:21
 * @Description
 **/
@Configuration
@Slf4j
public class GrayConfiguration {

    public GrayConfiguration() {
        log.info("启动灰度配置...");
    }


    @Bean
    @ConditionalOnProperty(value = "global.config.gray.type", havingValue = "nacos", matchIfMissing = true)
    @ConditionalOnMissingBean
    public GrayConfig nacosGrayConfig() {
        return new NacosListener();
    }

    @Bean
    @ConditionalOnProperty(value = "global.config.gray.type", havingValue = "apollo")
    @ConditionalOnMissingBean
    public GrayConfig apolloGrayConfig() {
        return new ApolloListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignGrayRequestInterceptor feignGrayRequestInterceptor() {
        return new FeignGrayRequestInterceptor();
    }

}
