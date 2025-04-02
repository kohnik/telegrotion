package com.fizzly.backend.repository;

import com.fizzly.backend.entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizSessionRepository extends JpaRepository<QuizSession, UUID> {
}
