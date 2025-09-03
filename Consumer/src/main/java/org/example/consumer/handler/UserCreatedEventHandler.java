package org.example.consumer.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.consumer.event.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "create-user-event-topic")
public class UserCreatedEventHandler {

    @KafkaHandler
    public void handle(UserCreatedEvent userCreatedEvent) {
        log.info("Пришел UserCreatedEvent {}", userCreatedEvent.getId());
    }
}
