package com.fizzly.backend.controller.quiz;

import com.fizzly.backend.dto.quiz.QuizCreateDTO;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.mapper.QuizMapper;
import com.fizzly.backend.service.quiz.QuizService;
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
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@Tag(name = "Квиз", description = "API управления квиз")
public class QuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;

    @GetMapping("/users/{userId}")
    @Operation(summary = "Получить все квизы пользователя")
    public List<Quiz> getAllQuizByUserId(@PathVariable("userId") Long userId) {
        return quizService.findAllQuizzesByUser(userId);
    }

    @PostMapping
    @Operation(summary = "Создать новый квиз")
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizCreateDTO quiz) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                quizService.createQuiz(quizMapper.toQuiz(quiz), quiz.getUserId())
        );
    }
}
