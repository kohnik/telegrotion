package com.fizzly.backend.exception;

public class QuizNotFoundException extends EntityNotFoundException {

    public QuizNotFoundException(Long quizId) {
        super(String.format("Quiz with id %s not found", quizId));
    }
}
