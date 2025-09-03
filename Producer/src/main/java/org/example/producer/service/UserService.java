package org.example.producer.service;

import lombok.RequiredArgsConstructor;
import org.example.producer.dao.UserRepository;
import org.example.producer.dto.UserDto;
import org.example.producer.dto.UserRequestDto;
import org.example.producer.mapper.UserMapper;
import org.example.producer.model.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto addUser(UserRequestDto dto) {
        User user = userMapper.toEntity(dto);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

}
