package com.relengxing.rebase.kafka.interceptor;

import com.relengxing.rebase.constant.GrayConstant;
import com.relengxing.rebase.gray.context.GrayContext;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Headers;

import java.util.Map;

public class KafkaProducerInterceptor implements ProducerInterceptor<String, String> {
    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        Headers headers = record.headers();
        if (GrayContext.get() != null) {
            headers.add(GrayConstant.GRAY_HEADER, GrayContext.get().getBytes());
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
