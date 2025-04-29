package com.fizzly.backend.entity.quiz.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizSessionRoom implements Serializable {

    private UUID roomId;
    private String joinCode;
    private boolean active;
    private Long quizId;
}
