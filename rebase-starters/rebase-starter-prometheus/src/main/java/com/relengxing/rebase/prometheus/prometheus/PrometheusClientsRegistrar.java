package com.relengxing.rebase.prometheus.prometheus;


import com.relengxing.rebase.prometheus.prometheus.collector.ReCollector;
import com.relengxing.rebase.prometheus.prometheus.util.MonitorUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author relengxing
 */
@Configuration
public class PrometheusClientsRegistrar implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ReCollector> collectors = applicationContext.getBeansOfType(ReCollector.class);
        if(! CollectionUtils.isEmpty(collectors)){
            collectors.forEach((key, value) -> {
                // 所有的自定义指标
                MonitorUtil.addMetrics(value);
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name}") String applicationName){
        // 对每个项目打上 tag
        return registry -> registry.config().commonTags("application", applicationName);
    }
}
