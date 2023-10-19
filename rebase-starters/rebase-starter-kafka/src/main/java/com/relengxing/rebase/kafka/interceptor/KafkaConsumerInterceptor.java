package com.relengxing.rebase.kafka.interceptor;

import com.relengxing.rebase.constant.GrayConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class KafkaConsumerInterceptor implements ConsumerInterceptor<String, String> {



    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        if (records.iterator().hasNext()) {
            ConsumerRecord<String, String> consumerRecord = records.iterator().next();
            Headers headers = consumerRecord.headers();
            Header header = headers.lastHeader(GrayConstant.GRAY_HEADER);
            if (header != null) {
                String messageColor = new String(header.value(), StandardCharsets.UTF_8);
                GrayContext.set(messageColor);
            }
        }
        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {
    }
}
