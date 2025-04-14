package com.fizzly.backend.service.quiz;

import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizQuestion;
import com.fizzly.backend.exception.QuizNotFoundException;
import com.fizzly.backend.repository.QuizQuestionRepository;
import com.fizzly.backend.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizQuestionService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizRepository quizRepository;

    public QuizQuestion addQuestionToQuiz(QuizQuestion quizQuestion, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));

        quizQuestion.setQuiz(quiz);

        return quizQuestionRepository.save(quizQuestion);
    }

    public List<QuizQuestion> findAllByQuizId(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));

        return quizQuestionRepository.findAllByQuiz(quiz);
    }

    @Transactional
    public void deleteQuestions(List<QuizQuestion> questions) {
        quizQuestionRepository.deleteAll(questions);
    }
}
