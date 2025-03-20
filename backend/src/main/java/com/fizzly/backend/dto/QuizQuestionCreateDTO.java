package com.fizzly.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizQuestionCreateDTO {

    private String question;
    private Long quizId;
}
