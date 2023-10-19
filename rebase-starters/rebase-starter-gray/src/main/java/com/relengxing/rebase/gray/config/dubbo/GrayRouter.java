package com.relengxing.rebase.gray.config.dubbo;

import cn.hutool.extra.spring.SpringUtil;
import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.gray.properties.GrayProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.router.AbstractRouter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 灰度 路由
 *
 * @author relengxing
 */
@Slf4j
public class GrayRouter extends AbstractRouter {
    private static final int TAG_ROUTER_DEFAULT_PRIORITY = 150;

    public GrayRouter(URL url) {
        this.url = url;
        this.priority = TAG_ROUTER_DEFAULT_PRIORITY;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public <T> List<Invoker<T>> route(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        if (CollectionUtils.isEmpty(invokers)) {
            return invokers;
        }
        return filterTag(invokers, url, invocation);
    }

    /**
     * 过滤下游的服务
     *
     * @param invokers
     * @param url
     * @param invocation
     * @param <T>
     * @return
     */
    private <T> List<Invoker<T>> filterTag(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        List<Invoker<T>> result = invokers;
        GrayConfig grayConfig = SpringUtil.getBean(GrayConfig.class);
        GrayProperties grayProperties = grayConfig.getGrayConfig();
        // 如果开启了灰度
        if (Boolean.TRUE.equals(grayProperties.getStatus())) {
            // 下游服务名
            String serviceName = invokers.stream().findFirst().map(invoker -> invoker.getUrl().getParameter(BaseConstant.DUBBO_SERVICE_KEY)).orElse("").trim();
            // 准备路由的版本号
            // 灰度名单包含下游服务
            if (grayProperties.getList().containsKey(serviceName)) {
                // 过滤出符合灰度版本的invokers
                String version = grayProperties.getVersion(serviceName);
                invokers = invokers.stream().filter(invoker -> invoker.getUrl().getParameter(BaseConstant.DUBBO_VERSION_KEY).equals(version)).collect(Collectors.toList());
            }
            result = invokers;
        }
        return result;
    }


    @Override
    public <T> void notify(List<Invoker<T>> invokers) {
        super.notify(invokers);
    }

    @Override
    public int compareTo(Router o) {
        return super.compareTo(o);
    }


}
