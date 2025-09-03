package org.example.producer.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.producer.dao.UserRepository;
import org.example.producer.dto.UserDto;
import org.example.producer.dto.UserRequestDto;
import org.example.producer.mapper.UserMapper;
import org.example.producer.model.User;
import org.example.producer.service.event.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserDto addUser(UserRequestDto dto) {
        User user = userMapper.toEntity(dto);
        userRepository.save(user);
        UserCreatedEvent userCreatedEvent = userMapper.toCreatedEvent(user);

        CompletableFuture<SendResult<String, UserCreatedEvent>> future = kafkaTemplate
                .send("create-user-event-topic", user.getId().toString(), userCreatedEvent);

        future.whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Ошибка при отправке сообщения: {}", exception.getMessage());
            } else {
                log.info("Сообщение отправлено успешно: {}", result.getRecordMetadata());
            }
        });
        log.info("Return result: {}", future);
        return userMapper.toDto(user);
    }
}
        /* Если мы хотим синхронно. Используем вместо CompletableFuture

        SendResult<String, UserCreatedEvent> result = kafkaTemplate
                .send("create-user-event-topic", user.getId().toString(), userCreatedEvent).get();
        log.info("Topic: {}", result.getRecordMetadata().topic());
        log.info("Partition: {}", result.getRecordMetadata().partition());

         */
