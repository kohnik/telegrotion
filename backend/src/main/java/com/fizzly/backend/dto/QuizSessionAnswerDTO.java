package com.fizzly.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSessionAnswerDTO {
    private String answer;
    private int order;
    private boolean isCorrect;
}
