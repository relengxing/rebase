package com.relengxing.rebase.dubbo;
import cn.hutool.extra.spring.SpringUtil;
import com.relengxing.rebase.constant.BaseConstant;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * 服务提供方  注册信息包装
 *
 * @author relengxing
 */
public class ProviderRegistryWrapper implements RegistryFactory {

    private final RegistryFactory registryFactory;

    public ProviderRegistryWrapper(RegistryFactory registryFactory) {
        this.registryFactory = registryFactory;
    }

    @Override
    public Registry getRegistry(URL url) {
        return new RegistryWrapper(registryFactory.getRegistry(url));
    }

    static class RegistryWrapper implements Registry {
        private final Registry originRegistry;

        private URL appendProviderTag(URL url) {
            String side = url.getParameter(CommonConstants.SIDE_KEY);
            if (CommonConstants.PROVIDER_SIDE.equals(side)) {
                Resource location = new ClassPathResource("META-INF/build-info.properties");
                try {
                    Properties properties = PropertiesLoaderUtils.loadProperties(location);
                    String version = properties.getProperty("build.version").trim();
                    String applicationName = SpringUtil.getProperty("spring.application.name").trim();
                    url = url.addParameter(BaseConstant.DUBBO_VERSION_KEY, version);
                    url = url.addParameter(BaseConstant.DUBBO_SERVICE_KEY, applicationName);
                } catch (IOException e) {
                    throw new RuntimeException("无法读取 build-info.properties");
                }
            }
            return url;
        }

        public RegistryWrapper(Registry originRegistry) {
            this.originRegistry = originRegistry;
        }

        @Override
        public URL getUrl() {
            return originRegistry.getUrl();
        }

        @Override
        public boolean isAvailable() {
            return originRegistry.isAvailable();
        }

        @Override
        public void destroy() {
            originRegistry.destroy();
        }

        @Override
        public void register(URL url) {
            originRegistry.register(appendProviderTag(url));
        }

        @Override
        public void unregister(URL url) {
            originRegistry.unregister(appendProviderTag(url));
        }

        @Override
        public void subscribe(URL url, NotifyListener listener) {
            originRegistry.subscribe(url, listener);
        }

        @Override
        public void unsubscribe(URL url, NotifyListener listener) {
            originRegistry.unsubscribe(url, listener);
        }

        @Override
        public List<URL> lookup(URL url) {
            return originRegistry.lookup(appendProviderTag(url));
        }
    }

}
