package com.fizzly.backend.repository;

import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByOwner(User user);

}
