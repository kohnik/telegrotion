package com.fizzly.backend.controller;

import com.fizzly.backend.dto.QuizQuestionCreateDTO;
import com.fizzly.backend.entity.QuizQuestion;
import com.fizzly.backend.mapper.QuizQuestionMapper;
import com.fizzly.backend.service.QuizQuestionService;
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
public class QuizQuestionController {

    private final QuizQuestionService quizQuestionService;
    private final QuizQuestionMapper quizQuestionMapper;

    @PostMapping
    private ResponseEntity<QuizQuestion> addQuestionToQuiz(@RequestBody QuizQuestionCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                quizQuestionService.addQuestionToQuiz(
                        quizQuestionMapper.toQuizQuestion(createDTO), createDTO.getQuizId()
                )
        );
    }

    @GetMapping("/quizzes/{quizId}")
    private List<QuizQuestion> getQuestionFromQuiz(@PathVariable Long quizId) {
        return quizQuestionService.findAllByQuizId(quizId);
    }
}
