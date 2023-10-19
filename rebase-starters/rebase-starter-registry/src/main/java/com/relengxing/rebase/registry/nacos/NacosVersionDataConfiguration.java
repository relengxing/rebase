package com.relengxing.rebase.registry.nacos;

/**
 * @author relengxing
 * @date 2023-10-07 17:12
 * @Description
 **/

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.relengxing.rebase.constant.BaseConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

/**
 * 注册时携带版本号和启动时间
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore(NacosDiscoveryAutoConfiguration.class)
public class NacosVersionDataConfiguration {

    @Resource
    BuildProperties buildProperties;

    @Value("${spring.cloud.nacos.discovery.register-disabled:true}")
    Boolean disableRegister;

    @Bean
    @Primary
    public NacosDiscoveryProperties vvNacosDiscoveryProperties() {
        NacosDiscoveryProperties nacosDiscoveryProperties = new NacosDiscoveryProperties();
        Map<String, String> metadata = nacosDiscoveryProperties.getMetadata();
        // 启动时间
        metadata.put("startup.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date()));
        // 版本号
        metadata.put(BaseConstant.NACOS_VERSION_KEY, buildProperties.getVersion().trim());
        // 本地环境不注册
        if (Boolean.TRUE.equals(disableRegister) && isRegisterDisabled()) {
            nacosDiscoveryProperties.setRegisterEnabled(false);
        }
        return nacosDiscoveryProperties;
    }


    private boolean isRegisterDisabled() {
        // 本地ip并且在开发/测试环境做服务发现，则不注册
        return isLocalEnv() && discoveryOnDevOrTest();
    }


    private boolean isLocalEnv() {
        //本地网段 192.168 开头
        final String LOCAL_IP_PREFIX = "192.168";
        return getLocalIP().startsWith(LOCAL_IP_PREFIX);
    }

    @Value("${spring.cloud.nacos.discovery.namespace:}")
    private String namespace;

    @Value("${spring.cloud.nacos.discovery.server-addr:}")
    private String serverAddr;

    private boolean discoveryOnDevOrTest() {
        if (serverAddr.startsWith("127.0.0.1") || serverAddr.startsWith("localhost")) {
            return false;
        }
        final String DEV_ENV = "dev";
        final String TEST_ENV = "fat";
        final String UAT_ENV = "uat";
        final String PRO_ENV = "pro";
        return (DEV_ENV.equals(namespace) || TEST_ENV.equals(namespace)) || UAT_ENV.equals(namespace) || PRO_ENV.equals(namespace);
    }


    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }


}
