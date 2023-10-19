package com.relengxing.rebase.dubbo.pass;


import com.relengxing.rebase.context.PassThroughHolder;
import com.relengxing.rebase.pass.PassThroughLoaded;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;

import static org.apache.dubbo.rpc.Constants.LOCAL_PROTOCOL;

@Activate(group = {CommonConstants.PROVIDER})
@Slf4j
public class DubboPassProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> attachments = invocation.getAttachments();
        Result invoke;
        try {
            if (PassThroughLoaded.passThroughLoaded()) {
                for (Map.Entry<String, String> entry : attachments.entrySet()) {
                    PassThroughHolder.put(entry.getKey(), entry.getValue());
                }
            }
            invoke = invoker.invoke(invocation);
        } finally {
            if (!isInjvm(invoker.getUrl())) {
                if (PassThroughLoaded.passThroughLoaded()) {
                    PassThroughHolder.clear();
                }
            }
        }
        return invoke;
    }


    public static boolean isInjvm(URL url) {
        if (url.getProtocol().equals(LOCAL_PROTOCOL)) {
            return true;
        }
        return false;
    }
}
