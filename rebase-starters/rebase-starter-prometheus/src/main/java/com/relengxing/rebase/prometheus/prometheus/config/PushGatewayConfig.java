package com.relengxing.rebase.prometheus.prometheus.config;

import cn.hutool.extra.spring.SpringUtil;
import com.relengxing.rebase.prometheus.prometheus.collector.ReCollectorRegistry;
import com.relengxing.rebase.prometheus.prometheus.util.InstanceUtil;
import io.prometheus.client.exporter.PushGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 *
 * @Version V1.0.0
 * @Date 2022/3/14
 */
@ConditionalOnProperty(name = "prometheus.push.gateway.enabled", matchIfMissing = true)
@Configuration
@EnableScheduling
@Slf4j
@EnableConfigurationProperties(PushGatewayProperties.class)
public class PushGatewayConfig implements SchedulingConfigurer {

    private boolean enabled;

    @Resource
    PushGatewayProperties pushGatewayProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        String active = SpringUtil.getBean(Environment.class).getProperty("spring.profiles.active");
        String name = SpringUtil.getBean(Environment.class).getProperty("spring.application.name");
        taskRegistrar.addTriggerTask(() -> {
            if (enabled) {
                try {
                    String address = pushGatewayProperties.getAddress();
                    if (StringUtils.hasText(address)) {
                        log.warn("pushGatewayProperties.address is empty...");
                    }
                    String instance = InstanceUtil.getInstance();
                    Map<String, String> map = new HashMap<>();
                    map.put("instance", instance);
                    map.put("profile", active);
                    PushGateway pushGateway = new PushGateway(address);
                    pushGateway.push(ReCollectorRegistry.getReCollectorRegistry(), name, map);
//                    log.info("PushGatewayConfig success...");
                } catch (Exception e) {
                    log.error("PushGatewayConfig error: ", e);
                }
            }
        }, triggerContext -> {
            enabled = pushGatewayProperties.isEnabled();
            String cron = pushGatewayProperties.getTime();
            CronTrigger trigger = new CronTrigger(cron);
            return trigger.nextExecutionTime(triggerContext);

        });


    }

}
