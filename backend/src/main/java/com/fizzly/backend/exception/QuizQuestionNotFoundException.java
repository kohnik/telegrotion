package com.fizzly.backend.exception;

public class QuizQuestionNotFoundException extends EntityNotFoundException {

    public QuizQuestionNotFoundException(Long questionId) {
        super(String.format("Quiz question with id %s not found", questionId));
    }
}
