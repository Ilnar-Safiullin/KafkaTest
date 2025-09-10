package org.example.consumer.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.consumer.event.UserCreatedEvent;
import org.example.consumer.exception.NonRetryableException;
import org.example.consumer.exception.RetryableException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@KafkaListener(topics = "create-user-event-topic")
@RequiredArgsConstructor
public class UserCreatedEventHandler {
    private RestTemplate restTemplate;


    @KafkaHandler
    public void handle(UserCreatedEvent userCreatedEvent) {

        try {
            log.info("Пришел UserCreatedEvent {}", userCreatedEvent.getId());
            System.out.println("Сделать какую то операцию");

        } catch (ResourceAccessException e) { // не доступен ресурс, есть смысл ретраев
            log.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NonRetryableException(e);
        }

    }
}
// То как выставить чтение по очереди?
/*
Депозит и 3 евента. Как прочесть в правильном порядке запись.
 */