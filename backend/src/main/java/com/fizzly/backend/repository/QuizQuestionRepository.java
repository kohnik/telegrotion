package com.fizzly.backend.repository;

import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findAllByQuiz(Quiz quiz);
}
