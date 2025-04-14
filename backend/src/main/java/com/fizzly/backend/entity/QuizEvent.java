package com.fizzly.backend.entity;

import lombok.Getter;

@Getter
public enum QuizEvent {
    QUIZ_STARTED(1L, "quizStarted"),
    QUESTION_ENDED(2L, "questionEnded"),
    ANSWER_SUBMITTED(3L, "answerSubmitted"),
    QUIZ_FINISHED(4L, "quizFinished"),
    NEW_QUESTION(5L, "newQuestion");

    private final Long id;
    private final String name;

    QuizEvent(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
