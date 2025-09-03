package org.example.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.producer.service.event.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put("spring.json.trusted.packages",
                "org.example.producer.service.event," +
                "org.example.consumer.event");
        configProps.put("spring.json.add.type.headers", false);

        // Кастомные настройки продюсера
        configProps.put(ProducerConfig.ACKS_CONFIG, "all"); // От скольких брокеров мы должны получить ответ (по умолч от Лидера) all/0/кол-во
        //configProps.put(ProducerConfig.RETRIES_CONFIG, 30); // кол-во ретраев (НЕ РЕКОМЕНДУЕТСЯ ТРОГАТЬ)
        //configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000); // с каким интервалом делать ретрай в МС (НЕ РЕКОМЕНДУЕТСЯ ТРОГАТЬ)
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // в течение какого времени мы будем пытаться ретраить. Должно быть >= linger.ms + request.timeout.ms
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 0); // в течение какого времени мы копим сообщения чтобы отправить одним батчем, по умолч 0 сразу шлем не копим
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000); // как долго мы будем ждать ответ от брокера кафки о доставке сообщения

        // Дополнительные настройки для надежности
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Идемпотентность, чтобы не было дупликатов сообщения. acks = all, retries > 0, per.connection <= 5
        configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // Макс параллельных запросов
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Сжатие

        return configProps;
    }

    @Bean
    ProducerFactory<String, UserCreatedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    KafkaTemplate<String, UserCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    NewTopic createTopic() {
        return TopicBuilder.name("create-user-event-topic")
                .partitions(3)
                .replicas(1)
                //min insync replicas указывать если несколько брокеров (мин-ое кол-во в синхроне с лидером)
                .build();
    }
}
