package com.fizzly.backend.controller;

import com.fizzly.backend.dto.QuizAnswerCreateDTO;
import com.fizzly.backend.dto.QuizAnswerMultipleCreateDTO;
import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.mapper.QuizAnswerMapper;
import com.fizzly.backend.service.QuizAnswerService;
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
@RequestMapping("/answers")
public class QuizAnswerController {

    private final QuizAnswerService quizAnswerService;
    private final QuizAnswerMapper quizAnswerMapper;

    @PostMapping
    public ResponseEntity<QuizAnswer> addAnswerToQuestion(@RequestBody QuizAnswerCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                quizAnswerService.addQuizAnswerToQuiz(
                        quizAnswerMapper.toQuizAnswer(createDTO), createDTO.getQuestionId()
                )
        );
    }

    @PostMapping("/all")
    public List<QuizAnswer> addMultipleAnswersToQuestion(@RequestBody QuizAnswerMultipleCreateDTO createDTO) {
        return quizAnswerService.addMultipleAnswersToQuiz(
                quizAnswerMapper.toQuizAnswerList(createDTO.getAnswers()), createDTO.getQuestionId()
        );
    }

    @GetMapping("/questions/{questionId}")
    public List<QuizAnswer> getAllAnswers(@PathVariable("questionId") Long questionId) {
        return quizAnswerService.getAllAnswersByQuestionId(questionId);
    }

}
