package com.relengxing.rebase.feign.gray;

import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.gray.properties.GrayProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 灰度负载均衡
 *
 * @author relengxing
 * @date 2023-10-19 21:52
 * @Description
 **/
@Slf4j
public class GrayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    final AtomicInteger position;


    // =============== 构造函数  start =================

    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        position = new AtomicInteger(1000);
    }

    public GrayLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, int seedPosition) {
        position = new AtomicInteger(1000);
    }

    // =============== 构造函数  end =================

    @Resource
    NacosDiscoveryClient nacosDiscoveryClient;

    @Resource
    GrayConfig grayConfig;


    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        GrayProperties grayProperties = grayConfig.getGrayConfig();
        DefaultRequest defaultRequest = (DefaultRequest) request;
        RequestDataContext requestDataContext = (RequestDataContext) defaultRequest.getContext();
        String serviceId = requestDataContext.getClientRequest().getUrl().getHost();
        log.debug(String.format("GrayLoadBalancer Choose %s", serviceId));
        List<ServiceInstance> instances = nacosDiscoveryClient.getInstances(serviceId);
        Response<ServiceInstance> instanceResponse;
        if (Boolean.TRUE.equals(grayProperties.getStatus())) {
            // 灰度
            // 获取服务的 ServiceInstance 列表
            if (grayProperties.getList().containsKey(serviceId)) {
                // 执行负载均衡算法，选择一个 ServiceInstance
                instanceResponse = getInstanceResponse(serviceId, instances);
                return Mono.just(instanceResponse);
            }
        }
        if (instances.isEmpty()) {
            instanceResponse = new EmptyResponse();
        } else if (instances.size() == 1) {
            instanceResponse = new DefaultResponse(instances.get(0));
        } else {
            int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
            ServiceInstance instance = instances.get(pos % instances.size());
            instanceResponse = new DefaultResponse(instance);
        }
        // 没开启灰度则轮询
        return Mono.just(instanceResponse);
    }

    /**
     * 使用随机数获取服务
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getInstanceResponse(String serviceId, List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }
        GrayProperties grayProperties = grayConfig.getGrayConfig();
        String version = grayProperties.getVersion(serviceId);

        // 命中灰度规则，那么实例只能选择对应灰度的服务
        if (!instances.isEmpty()) {
            instances = instances.stream()
                    .filter(instance -> instance.getMetadata().getOrDefault(BaseConstant.NACOS_VERSION_KEY, "0.0.0").equals(version))
                    .collect(Collectors.toList());
        }
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }
        if (instances.size() == 1) {
            return new DefaultResponse(instances.get(0));
        }
        int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
        ServiceInstance instance = instances.get(pos % instances.size());
        return new DefaultResponse(instance);
    }


}
