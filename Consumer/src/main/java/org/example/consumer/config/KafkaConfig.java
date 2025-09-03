package org.example.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.consumer.event.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "user-created-events");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // Важные настройки consumer
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // earliest/latest/none
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // Ручное управление коммитами
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500); // Максимум записей за один poll
        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 минут таймаут
        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000); // 10 секунд сессия
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000); // 3 секунды heartbeat

        // Настройки для JsonDeserializer
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES,
                "org.example.producer.service.event," +
                "org.example.consumer.event");
        configProps.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UserCreatedEvent.class);

        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}