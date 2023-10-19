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
        if (rpcContext.isConsumerSide()) {
            if (PassThroughLoaded.passThroughLoaded()) {
                for (Map.Entry<String, String> entry : PassThroughHolder.getAll().entrySet()) {
                    rpcContext.setAttachment(entry.getKey(), entry.getValue());
                }
            }
        }


        return invoker.invoke(invocation);
    }
}
