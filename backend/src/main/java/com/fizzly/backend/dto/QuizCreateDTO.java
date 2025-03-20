package com.fizzly.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizCreateDTO {

    private String name;
    private Long userId;
}
