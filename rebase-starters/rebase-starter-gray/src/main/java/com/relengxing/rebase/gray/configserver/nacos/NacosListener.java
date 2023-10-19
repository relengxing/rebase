package com.relengxing.rebase.gray.configserver.nacos;

import cn.hutool.setting.yaml.YamlUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.relengxing.rebase.gray.configserver.AbstractGrayConfigListener;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.gray.properties.GrayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.Properties;
import java.util.function.BiConsumer;


@Slf4j
public class NacosListener extends AbstractGrayConfigListener {
    private static final String NACOS_DATA_ID = "base-gray";

    @Autowired
    Environment environment;

    @Autowired
    NacosDiscoveryProperties nacosDiscoveryProperties;


    @PostConstruct
    public void init() throws NacosException {
        //获取ConfigService
        Properties properties = new Properties();
        String namespace = nacosDiscoveryProperties.getNamespace();
        properties.put(PropertyKeyConst.NAMESPACE, namespace);
        properties.put(PropertyKeyConst.SERVER_ADDR, nacosDiscoveryProperties.getServerAddr());
        ConfigService configService = NacosFactory.createConfigService(properties);
        configInfoLocal = configService.getConfigAndSignListener(NACOS_DATA_ID, nacosDiscoveryProperties.getGroup(), 10000, new AbstractListener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("刷新灰度配置: \n" + configInfo);
                configInfoLocal = configInfo;
                load();
            }
        });
        load();
        log.info("NacosListener: \n" + configInfoLocal);
    }


}
