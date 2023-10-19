package com.relengxing.rebase.dubbo.gray;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.cluster.Router;
import org.apache.dubbo.rpc.cluster.RouterFactory;

/**
 * 自定义灰度路由工厂
 *
 * @author relengxing
 */
@Activate(order = 100)
public class GrayRouterFactory implements RouterFactory {


    @Override
    public Router getRouter(URL url) {
        return new GrayRouter(url);
    }
}
