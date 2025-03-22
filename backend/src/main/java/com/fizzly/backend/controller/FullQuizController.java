package com.fizzly.backend.controller;

import com.fizzly.backend.dto.FullQuizCreateDTO;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.service.FullQuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
