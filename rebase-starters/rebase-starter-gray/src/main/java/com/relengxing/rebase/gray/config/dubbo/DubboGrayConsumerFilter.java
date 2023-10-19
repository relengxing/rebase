package com.relengxing.rebase.gray.config.dubbo;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import com.relengxing.rebase.gray.context.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.protocol.injvm.InjvmProtocol;
import org.springframework.boot.info.BuildProperties;

/**
 * @Description 过滤器传递灰度标识（消费者）

 * @Date 2022/10/5 15:24
 * @Version v1.0
 */
@Activate(group = {CommonConstants.CONSUMER})
@Slf4j
public class DubboGrayConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        String grayFlag = null;
        String traceFlag = null;
        if (rpcContext.isConsumerSide()) {//InjvmProtocol
            try {
                BuildProperties buildProperties = SpringUtil.getBean(BuildProperties.class);
                rpcContext.setAttachment(BaseConstant.DUBBO_CONSUMER_SERVICE_KEY, buildProperties.getName());
            } catch (Exception e) {
                log.error("DubboGrayConsumerFilter ",e);
            }
            grayFlag = GrayContext.get();
            if (grayFlag != null) {
                rpcContext.setAttachment(BaseConstant.GRAY_HEADER, grayFlag);
            }
            traceFlag = TraceContext.get();
            if (traceFlag != null) {
                rpcContext.setAttachment(BaseConstant.TRACE_HEADER, traceFlag);
                if (InjvmProtocol.getInjvmProtocol().isInjvmRefer(invoker.getUrl())) {
                    log.info("TraceContext Injvm call {}", JSONUtil.toJsonStr(invoker.getUrl().getParameters()));
                } else {
                    String ipPort = invoker.getUrl().getHost() + ":" + invoker.getUrl().getPort();
                    String service = invoker.getUrl().getParameter(BaseConstant.DUBBO_SERVICE_KEY);
                    String version = invoker.getUrl().getParameter(BaseConstant.DUBBO_VERSION_KEY);
                    log.info("TraceContext dubbo trace: {} 准备调用下游服务 service:{} version: {} ip:{} 灰度标识 {}", traceFlag, service, version, ipPort, grayFlag);
                    if (service == null) {
                        log.info("TraceContext 下游服务没有版本标识: {}", JSONUtil.toJsonStr(invoker.getUrl().getParameters()));
                    }
                }
            }
        }
        return invoker.invoke(invocation);
    }
}
