package com.relengxing.rebase.gray.config.dubbo;

import cn.hutool.json.JSONUtil;
import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import com.relengxing.rebase.gray.context.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.util.Map;

import static org.apache.dubbo.rpc.Constants.*;

/**
 * @Description 过滤器传递灰度标识（提供者）

 * @Date 2022/10/5 15:24
 * @Version v1.0
 */
@Activate(group = {CommonConstants.PROVIDER})
@Slf4j
public class DubboGrayProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> attachments = invocation.getAttachments();
        String grayFlag = attachments.get(BaseConstant.GRAY_HEADER);
        String traceFlag = attachments.get(BaseConstant.TRACE_HEADER);
        Result invoke;
        try {
            if (grayFlag != null) {
                GrayContext.set(grayFlag);
            }
            if (traceFlag != null) {
                TraceContext.set(traceFlag);
                String consumer = attachments.get(BaseConstant.DUBBO_CONSUMER_SERVICE_KEY);
                log.info("TraceContext dubbo trace: {}, 上游: {}, 灰度标识: {}", traceFlag, consumer, grayFlag);
            }
            invoke = invoker.invoke(invocation);
        } finally {
//            if (InjvmProtocol.getInjvmProtocol().isInjvmRefer(invoker.getUrl()) ) {
            if (isInjvm(invoker.getUrl())) {
                if (traceFlag != null) {
                    log.info("TraceContext Injvm call 不清空线程变量 {}", JSONUtil.toJsonStr(invoker.getUrl().getParameters()));
                }
            } else {
                GrayContext.clear();
                TraceContext.clear();
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
