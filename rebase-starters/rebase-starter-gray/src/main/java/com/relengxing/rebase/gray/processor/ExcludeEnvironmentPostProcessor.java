package com.relengxing.rebase.gray.processor;


import cn.hutool.core.collection.CollectionUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.*;

/**
 * 这个没用到了
 */
@Configuration
public class ExcludeEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.out.println("ExcludeEnvironmentPostProcessor");
        String key = "spring.autoconfigure.exclude";
        Binder binder = Binder.get(environment);
        List<String> exList = new ArrayList<>();
        // 1 先获取到原配置文件的信息。这里参考的AutoConfigurationImportSelector#getExcludeAutoConfigurationsProperty
        List<String> stringList = (List) binder.bind(key, String[].class).map(Arrays::asList).orElse(Collections.emptyList());
        exList.addAll(stringList);
        // 2 增加需要排除的类
        exList.add("org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration.BlockingLoadBalancerClient.BlockingLoadbalancerClientConfig");
        MutablePropertySources m = environment.getPropertySources();
        Properties p = new Properties();
        p.put(key, CollectionUtil.join(exList, ","));
        // 3 保存新的配置文件
        m.addFirst(new PropertiesPropertySource("commonDataProperties", p));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
