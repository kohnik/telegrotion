package by.fizzly.backend.repository;

import by.fizzly.backend.entity.Quiz;
import by.fizzly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByOwner(User user);

}
