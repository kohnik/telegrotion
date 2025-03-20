package com.fizzly.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizAnswerCreateDTO {

    private String answer;
    private boolean correct;
    private int points;
    private Long questionId;
}
