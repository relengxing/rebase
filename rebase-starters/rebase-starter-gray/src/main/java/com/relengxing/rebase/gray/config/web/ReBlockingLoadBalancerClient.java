/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.relengxing.rebase.gray.config.web;

import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import com.relengxing.rebase.gray.context.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.client.loadbalancer.reactive.*;
import org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;

/**
 * The default {@link LoadBalancerClient} implementation.
 * <p>
 * 这个类其实没有什么用，这个是为了解决一个老版本的问题，如果升级新版本可以不用这个了
 * 改了choose方法
 *
 * @author Olga Maciaszek-Sharma
 * @since 2.2.0
 */

@Slf4j
public class ReBlockingLoadBalancerClient extends BlockingLoadBalancerClient {

    private final LoadBalancerClientFactory loadBalancerClientFactory;

    public ReBlockingLoadBalancerClient(LoadBalancerClientFactory loadBalancerClientFactory) {
        super(loadBalancerClientFactory);
        this.loadBalancerClientFactory = loadBalancerClientFactory;
    }

    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request)
            throws IOException {
        ServiceInstance serviceInstance = choose(serviceId);
        if (serviceInstance == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        String traceFlag = TraceContext.get();
        if (traceFlag != null) {
            String ipPort = serviceInstance.getHost() + ":" + serviceInstance.getPort();
            String version = serviceInstance.getMetadata().get(BaseConstant.NACOS_VERSION_KEY);
            log.info("TraceContext web trace: {} 准备调用下游服务 service:{} version: {} ip:{} 灰度标识 {}", traceFlag, serviceId, version, ipPort, GrayContext.get());
        }
        return execute(serviceId, serviceInstance, request);
    }

    @Override
    public ServiceInstance choose(String serviceId) {
        ReactiveLoadBalancer<ServiceInstance> loadBalancer = this.loadBalancerClientFactory
                .getInstance(serviceId);
        if (loadBalancer == null) {
            return null;
        }
        String url = "lb://" + serviceId;
        URI uri = URI.create(url).normalize();
        Request<DefaultRequestContext> request = new DefaultRequest(uri);
        Response<ServiceInstance> loadBalancerResponse = Mono.from(loadBalancer.choose(request)).block();
        if (loadBalancerResponse == null) {
            return null;
        }
        return loadBalancerResponse.getServer();
    }

}
