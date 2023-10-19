package com.relengxing.rebase.dubbo.pass;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.relengxing.rebase.context.PassThroughHolder;
import com.relengxing.rebase.pass.PassThroughLoaded;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.injvm.InjvmProtocol;
import org.springframework.boot.info.BuildProperties;

import java.util.Map;

@Activate(group = {CommonConstants.CONSUMER})
@Slf4j
public class DubboPassConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        if (rpcContext.isConsumerSide()) {//InjvmProtocol
            try {
                BuildProperties buildProperties = SpringUtil.getBean(BuildProperties.class);
                rpcContext.setAttachment(GrayConstant.DUBBO_CONSUMER_SERVICE_KEY, buildProperties.getName());
            } catch (Exception e) {
                log.error("DubboGrayConsumerFilter ",e);
            }
            grayFlag = GrayContext.get();
            if (grayFlag != null) {
                rpcContext.setAttachment(GrayConstant.GRAY_HEADER, grayFlag);
            }
            traceFlag = TraceContext.get();
            if (traceFlag != null) {
                rpcContext.setAttachment(GrayConstant.TRACE_HEADER, traceFlag);
                if (InjvmProtocol.getInjvmProtocol().isInjvmRefer(invoker.getUrl())) {
                    log.info("TraceContext Injvm call {}", JSONUtil.toJsonStr(invoker.getUrl().getParameters()));
                } else {
                    String ipPort = invoker.getUrl().getHost() + ":" + invoker.getUrl().getPort();
                    String service = invoker.getUrl().getParameter(GrayConstant.DUBBO_SERVICE_KEY);
                    String version = invoker.getUrl().getParameter(GrayConstant.DUBBO_VERSION_KEY);
                    log.info("TraceContext dubbo trace: {} 准备调用下游服务 service:{} version: {} ip:{} 灰度标识 {}", traceFlag, service, version, ipPort, grayFlag);
                    if (service == null) {
                        log.info("TraceContext 下游服务没有版本标识: {}", JSONUtil.toJsonStr(invoker.getUrl().getParameters()));
                    }
                }
            }
            if (PassThroughLoaded.passThroughLoaded()) {
                for (Map.Entry<String, String> entry : PassThroughHolder.getAll().entrySet()) {
                    rpcContext.setAttachment(entry.getKey(), entry.getValue());
                }
            }
        }


        return invoker.invoke(invocation);
    }
}
