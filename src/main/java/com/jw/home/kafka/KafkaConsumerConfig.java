package com.jw.home.kafka;

import com.jw.home.kafka.dto.DeviceStateValue;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;
    @Value(value = "${spring.kafka.topics.device-state}")
    private String deviceStateTopicName;
    @Value(value = "${spring.kafka.consumer.group-id}")
    private String consumerGroupId;

    @Bean
    public KafkaReceiver<String, DeviceStateValue> deviceStateReceiver() {
        Map<String,Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
//        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions<String, DeviceStateValue> receiverOptions =
                ReceiverOptions.<String, DeviceStateValue>create(configs)
                        .withKeyDeserializer(new StringDeserializer())
                        .withValueDeserializer(new JsonDeserializer<>(DeviceStateValue.class))
                        .subscription(Collections.singleton(deviceStateTopicName));
        return KafkaReceiver.create(receiverOptions);
    }
}
