package com.relengxing.rebase.prometheus.prometheus.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;


@Configuration
@Order
public class PrometheusPostProcessor implements EnvironmentPostProcessor {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    public static final String PROMETHEUS_FILE = "prometheus.yaml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource path = new ClassPathResource(PROMETHEUS_FILE);
        if (!path.exists()) {
            throw new IllegalArgumentException("PrometheusPostProcessor: Resource " + path + " does not exists");
        }
        try {
            List<PropertySource<?>> load = loader.load(PROMETHEUS_FILE, path);
            for (PropertySource<?> propertySource : load) {
                environment.getPropertySources().addLast(propertySource);
            }
            System.out.println("PrometheusPostProcessor: 已加载 {" + PROMETHEUS_FILE + "} 配置文件");
        } catch (IOException e) {
            throw new IllegalArgumentException("PrometheusPostProcessor: Failed to load yaml configuration from " + path, e);
        }
    }
}
