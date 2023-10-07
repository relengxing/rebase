package com.relengxing.rebase.gray.configserver.nacos;

import cn.hutool.setting.yaml.YamlUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.alibaba.nacos.api.exception.NacosException;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.gray.properties.GrayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.Properties;


@Slf4j
public class NacosListener implements GrayConfig {
    private static final String NACOS_DATA_ID = "base-gray";

    /**
     * 原始字符串
     */
    String configInfoLocal;

    /**
     * 转换后的对象
     */
    GrayProperties grayProperties;

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

    @Override
    public GrayProperties getGrayConfig() {
        return grayProperties;
    }


    private void load() {
        if (configInfoLocal == null) {
            // 没有配置则认为没有打开蓝绿
            grayProperties = new GrayProperties();
            grayProperties.setStatus(false);
            return;
        }
        try {
            grayProperties = YamlUtil.load(new ByteArrayInputStream(configInfoLocal.getBytes()), GrayProperties.class);
        } catch (Exception e) {
            log.error("蓝绿配置文件解析错误，无法开启，请联系运维");
        }
    }

}
