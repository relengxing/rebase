package com.relengxing.rebase.kafka.interceptor;

import com.relengxing.rebase.context.PassThroughHolder;
import com.relengxing.rebase.pass.PassThroughLoaded;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

import java.util.Map;


/**
 * kafka 生产者拦截器
 */
public class KafkaProducerInterceptor implements ProducerInterceptor<String, String> {
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Headers headers = record.headers();
        if (PassThroughLoaded.passThroughLoaded()) {
            for (Map.Entry<String, String> entry : PassThroughHolder.getAll().entrySet()) {
                headers.add(entry.getKey(), entry.getValue().getBytes());
            }
        }
        return new ProducerRecord<>(
                record.topic(),
                record.partition(),
                record.timestamp(),
                record.key(),
                record.value(),
                headers);

    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
