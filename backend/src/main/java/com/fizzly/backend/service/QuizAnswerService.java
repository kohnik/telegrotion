package com.fizzly.backend.service;

import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.entity.QuizQuestion;
import com.fizzly.backend.exception.QuizQuestionNotFoundException;
import com.fizzly.backend.repository.QuizAnswerRepository;
import com.fizzly.backend.repository.QuizQuestionRepository;
import com.fizzly.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAnswerService {

    private final QuizAnswerRepository answerRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    public QuizAnswer addQuizAnswerToQuiz(QuizAnswer quizAnswer, Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        quizAnswer.setQuestion(question);

        return answerRepository.save(quizAnswer);
    }

    public List<QuizAnswer> addMultipleAnswersToQuiz(List<QuizAnswer> answers, Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        answers.forEach(answer -> answer.setQuestion(question));

        return answerRepository.saveAll(answers);
    }

    public List<QuizAnswer> getAllAnswersByQuestionId(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        return answerRepository.findAllByQuestion(question);
    }
}
