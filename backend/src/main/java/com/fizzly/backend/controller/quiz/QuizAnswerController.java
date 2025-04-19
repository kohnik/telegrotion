package com.fizzly.backend.controller.quiz;

import com.fizzly.backend.dto.quiz.QuizAnswerCreateDTO;
import com.fizzly.backend.dto.quiz.QuizAnswerMultipleCreateDTO;
import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.mapper.QuizAnswerMapper;
import com.fizzly.backend.service.quiz.QuizAnswerService;
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
@RequestMapping("/answers")
@Tag(name = "Ответы на вопросы", description = "API управления ответами на вопросы для квиза")
public class QuizAnswerController {

    private final QuizAnswerService quizAnswerService;
    private final QuizAnswerMapper quizAnswerMapper;

    @PostMapping
    @Operation(summary = "Добавить вариант ответа к вопросу")
    public ResponseEntity<QuizAnswer> addAnswerToQuestion(@RequestBody QuizAnswerCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                quizAnswerService.addQuizAnswerToQuiz(
                        quizAnswerMapper.toQuizAnswer(createDTO), createDTO.getQuestionId()
                )
        );
    }

    @PostMapping("/all")
    @Operation(summary = "Добавить несколько вариантов ответа к вопросу")
    public List<QuizAnswer> addMultipleAnswersToQuestion(@RequestBody QuizAnswerMultipleCreateDTO createDTO) {
        return quizAnswerService.addMultipleAnswersToQuiz(
                quizAnswerMapper.toQuizAnswerList(createDTO.getAnswers()), createDTO.getQuestionId()
        );
    }

    @GetMapping("/questions/{questionId}")
    @Operation(summary = "Получить все варианты ответов оперделенного вопроса")
    public List<QuizAnswer> getAllAnswers(@PathVariable("questionId") Long questionId) {
        return quizAnswerService.getAllAnswersByQuestionId(questionId);
    }

}
