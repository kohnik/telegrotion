package by.fizzly.backend.controller;

import by.fizzly.common.dto.UserCreateDTO;
import by.fizzly.backend.entity.User;
import by.fizzly.backend.mapper.UserMapper;
import by.fizzly.backend.service.UserService;
import by.fizzly.common.dto.UserGetDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользователь", description = "API управления пользователями")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Создать пользователя")
    public ResponseEntity<User> createUser(@RequestBody UserCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userService.save(userMapper.toUser(createDTO))
        );
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public List<UserGetDTO> findAllUsers() {
        return userService.getAllUsers().stream()
                .map(userMapper::toUserGetDTO)
                .toList();
    }

}
