package by.fizzly.backend.repository;

import by.fizzly.backend.entity.QuizQuestion;
import by.fizzly.backend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findAllByQuiz(Quiz quiz);
}
