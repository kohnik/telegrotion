package com.fizzly.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizAnswerMultipleCreateDTO {

    private List<QuizAnswerCreateDTO> answers;
    private Long questionId;
}
