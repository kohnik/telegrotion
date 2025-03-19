package com.telegrotion.backend.controller;

import com.telegrotion.backend.dto.UserCreateDTO;
import com.telegrotion.backend.entity.User;
import com.telegrotion.backend.mapper.UserMapper;
import com.telegrotion.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.save(userMapper.toUser(createDTO))
        );
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.getAllUsers();
    }

}
