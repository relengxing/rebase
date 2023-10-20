package com.relengxing.rebase.kafka.interceptor;

import com.relengxing.rebase.context.PassThroughHolder;
import com.relengxing.rebase.pass.PassThroughLoaded;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;


/**
 * kafka 消费者拦截器
 */
@Slf4j
public class KafkaConsumerInterceptor implements ConsumerInterceptor<String, String> {



    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {
        if (PassThroughLoaded.passThroughLoaded()) {
            if (records.iterator().hasNext()) {
                ConsumerRecord<String, String> consumerRecord = records.iterator().next();
                Headers headers = consumerRecord.headers();
                Iterator<Header> it = headers.iterator();
                while (it.hasNext()) {
                    Header next = it.next();
                    PassThroughHolder.put(next.key(), new String(next.value(), StandardCharsets.UTF_8));
                }
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
