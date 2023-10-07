package com.relengxing.rebase.prometheus.prometheus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "prometheus.push.gateway")
public class PushGatewayProperties {

    private boolean enabled = false;
    private String address;
    private String time = "0 0/1 * * * ?";
}
