package com.fizzly.backend.controller.quiz;

import com.fizzly.backend.dto.quiz.QuizQuestionCreateDTO;
import com.fizzly.backend.entity.QuizQuestion;
import com.fizzly.backend.mapper.QuizQuestionMapper;
import com.fizzly.backend.service.quiz.QuizQuestionService;
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
@RequiredArgsConstructor
@RequestMapping("/questions")
@Tag(name = "Вопросы", description = "API управления вопросами квиза")
public class QuizQuestionController {

    private final QuizQuestionService quizQuestionService;
    private final QuizQuestionMapper quizQuestionMapper;

    @PostMapping
    @Operation(summary = "Добавить вопрос к квизу")
    private ResponseEntity<QuizQuestion> addQuestionToQuiz(@RequestBody QuizQuestionCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                quizQuestionService.addQuestionToQuiz(
                        quizQuestionMapper.toQuizQuestion(createDTO), createDTO.getQuizId()
                )
        );
    }

    @GetMapping("/quizzes/{quizId}")
    @Operation(summary = "Получить все вопросы квиза")
    private List<QuizQuestion> getQuestionFromQuiz(@PathVariable Long quizId) {
        return quizQuestionService.findAllByQuizId(quizId);
    }
}
