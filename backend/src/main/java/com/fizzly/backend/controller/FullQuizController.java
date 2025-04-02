package com.fizzly.backend.controller;

import com.fizzly.backend.dto.FullQuizCreateDTO;
import com.fizzly.backend.dto.FullQuizGetDTO;
import com.fizzly.backend.dto.GetListQuizDTO;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.service.FullQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/full-quiz")
@RequiredArgsConstructor
@Tag(name = "Полное управление квизом", description = "API управления квизом")
public class FullQuizController {

    private final FullQuizService fullQuizService;

    @PostMapping
    @Operation(summary = "Создать полностью квиз")
    public ResponseEntity<Quiz> createFullQuiz(@RequestBody FullQuizCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fullQuizService.createFullQuiz(createDTO));
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "Получить полный квиз")
    public ResponseEntity<FullQuizGetDTO> getQuizById(@PathVariable("quizId") Long quizId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(fullQuizService.getFullQuiz(quizId));
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Получить полный список квизов по пользователю")
    public List<GetListQuizDTO> getQuizzesList(@PathVariable("userId") Long userId) {
        return fullQuizService.getAllQuizzesByUserId(userId);
    }

}
