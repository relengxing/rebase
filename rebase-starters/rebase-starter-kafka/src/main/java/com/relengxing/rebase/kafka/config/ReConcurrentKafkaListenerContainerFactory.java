package com.relengxing.rebase.kafka.config;

import org.springframework.kafka.config.AbstractKafkaListenerEndpoint;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReConcurrentKafkaListenerContainerFactory<K, V> extends ConcurrentKafkaListenerContainerFactory<K, V> {

    List<KafkaListenerEndpoint> endpointList = new ArrayList<>();


    @Override
    protected ConcurrentMessageListenerContainer<K, V> createContainerInstance(KafkaListenerEndpoint endpoint) {
        // 设置不自动启动
        AbstractKafkaListenerEndpoint<?, ?> absEndpoint = (AbstractKafkaListenerEndpoint<?, ?>) endpoint;
        absEndpoint.setAutoStartup(false);
        // 备份
        endpointList.add(endpoint);

        TopicPartitionOffset[] topicPartitions = endpoint.getTopicPartitionsToAssign();
        if (topicPartitions != null && topicPartitions.length > 0) {
            ContainerProperties properties = new ContainerProperties(topicPartitions);
            return new ConcurrentMessageListenerContainer<>(getConsumerFactory(), properties);
        }
        else {
            Collection<String> topics = endpoint.getTopics();
            if (!topics.isEmpty()) {
                ContainerProperties properties = new ContainerProperties(topics.toArray(new String[0]));
                return new ConcurrentMessageListenerContainer<>(getConsumerFactory(), properties);
            }
            else {
                ContainerProperties properties = new ContainerProperties(endpoint.getTopicPattern());
                return new ConcurrentMessageListenerContainer<>(getConsumerFactory(), properties);
            }
        }
    }

    public List<KafkaListenerEndpoint> getEndpointList() {
        return endpointList;
    }
}
