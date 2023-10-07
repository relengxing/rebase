package com.relengxing.rebase.gray.configserver;

import com.relengxing.rebase.gray.enums.ConfigServerEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "global.config.gray")
@Slf4j
public class GrayConfigProperties {

    private ConfigServerEnum type = ConfigServerEnum.NACOS;

    private Nacos nacos = new Nacos();
    private Apollo apollo = new Apollo();


    @Data
    public static class Nacos {
        private String group = "DEFAULT_GROUP";

        private String dataId = "base-gray";

        private String serverAddress = "127.0.0.1:8848";

        private String namespace;
    }

    @Data
    public static class Apollo {
        private String group = "DEFAULT_GROUP";

        private String dataId = "base-gray";

        private String serverAddress = "127.0.0.1:8848";

        private String namespace;
    }


    public GrayConfigProperties() {
        log.info("GrayConfigProperties created");
    }


}
