package com.relengxing.rebase.graceful;

import lombok.extern.slf4j.Slf4j;
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


@Slf4j
@Configuration
@Order
public class GracefulPostProcessor implements EnvironmentPostProcessor {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    public static final String GRACEFUL_FILE = "graceful.yaml";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource path = new ClassPathResource(GRACEFUL_FILE);
        if (!path.exists()) {
            throw new IllegalArgumentException("GracefulPostProcessor: Resource " + path + " does not exists");
        }
        try {
            List<PropertySource<?>> load = loader.load(GRACEFUL_FILE, path);
            for (PropertySource<?> propertySource : load) {
                environment.getPropertySources().addLast(propertySource);
            }
            System.out.println("GracefulPostProcessor: 已加载 {" + GRACEFUL_FILE + "} 配置文件");
        } catch (IOException e) {
            throw new IllegalArgumentException("GracefulPostProcessor: Failed to load yaml configuration from " + path, e);
        }
    }
}
