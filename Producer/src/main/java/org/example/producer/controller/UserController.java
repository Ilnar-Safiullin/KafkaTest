package org.example.producer.controller;

import lombok.RequiredArgsConstructor;
import org.example.producer.dto.UserDto;
import org.example.producer.dto.UserRequestDto;
import org.example.producer.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserRequestDto dto) throws ExecutionException, InterruptedException {
        return userService.addUser(dto);
    }
}