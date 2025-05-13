package by.fizzly.backend.repository;

import by.fizzly.backend.entity.QuizAnswer;
import by.fizzly.backend.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, Long> {

    List<QuizAnswer> findAllByQuestion(QuizQuestion quizQuestion);

}
