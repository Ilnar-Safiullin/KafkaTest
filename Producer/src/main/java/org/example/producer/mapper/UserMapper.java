package org.example.producer.mapper;

import org.example.producer.dto.UserDto;
import org.example.producer.dto.UserRequestDto;
import org.example.producer.model.User;
import org.example.producer.service.event.UserCreatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto dto);

    UserCreatedEvent toCreatedEvent(User user);
}
