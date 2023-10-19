package com.relengxing.rebase.kafka.config;

import cn.hutool.extra.spring.SpringUtil;
import com.relengxing.rebase.constant.BaseConstant;
import com.relengxing.rebase.gray.configserver.GrayConfig;
import com.relengxing.rebase.kafka.constant.KafkaConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;


@Slf4j
public class GrayRecordFilterStrategy implements RecordFilterStrategy<Object, Object> {


    private final String factoryType;
    GrayConfig grayConfig;

    private String currentColor;

    public GrayRecordFilterStrategy(String factoryType) {
        this.factoryType = factoryType;
        grayConfig = SpringUtil.getBean(GrayConfig.class);
        currentColor = grayConfig.currentColor();
        // 注册监听事件
        grayConfig.registerListener((grayProperties, grayProperties2) -> currentColor = grayConfig.currentColor());
    }

    @Override
    public boolean filter(ConsumerRecord<Object, Object> consumerRecord) {
        // 开启蓝绿，且当前服务在灰度名单
        if (grayConfig.getGrayConfig().getStatus() && grayConfig.inGrayList()) {
            Headers headers = consumerRecord.headers();
            Header header = headers.lastHeader(BaseConstant.GRAY_HEADER);
            String messageColor = "";
            if (header != null) {
                messageColor = new String(header.value(), StandardCharsets.UTF_8);
            }
            if (StringUtils.isEmpty(messageColor)) {
                // 未染色消息, 内部容器，不过滤
                if (factoryType.equals(KafkaConstant.INTERNAL)) {
                    return false;
                }
            } else {
                // 染色消息,灰度容器,且颜色匹配，不过滤
                if (factoryType.equals(KafkaConstant.GRAY) && (messageColor.equals(currentColor))) {
                    return false;
                }
            }
            // 其他情况过滤
            log.info(factoryType + ":  过滤消息 {}", consumerRecord.toString());
            return true;
        }
        return false;
    }
}
