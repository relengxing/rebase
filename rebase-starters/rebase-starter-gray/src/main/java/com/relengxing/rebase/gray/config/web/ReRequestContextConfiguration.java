package com.relengxing.rebase.gray.config.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 通过这个类，引入拦截器
 *
 * @author relengxing
 * @date 2023-05-21 11:38
 * @Description
 **/
@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class ReRequestContextConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //
        GrayFlagInterceptor grayFlagInterceptor = new GrayFlagInterceptor();

        InterceptorRegistration registration = registry.addInterceptor(grayFlagInterceptor);
        registration.addPathPatterns("/**");//所有路径都被拦截
        registration.excludePathPatterns(//添加不拦截路径
                "/**/*.html",            //html静态资源
                "/**/*.js",              //js静态资源
                "/**/*.css",             //css静态资源
                "/**/*.ico",             //ico图标
                "/**/*.woff",
                "/**/*.ttf"
        );
    }


}
