package com.fizzly.backend.repository;

import com.fizzly.backend.entity.QuizAnswer;
import com.fizzly.backend.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findAllByQuestion(QuizQuestion quizQuestion);

}
