package com.fizzly.backend.dto;

import com.fizzly.backend.entity.QuizEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizSessionDTO {
    private Long eventId = QuizEvent.NEW_QUESTION.getId();
    private Long questionId;
    private String questionName;
    private List<QuizSessionAnswerDTO> answers;
    private int timeLeft;
    private int points;
    private boolean isActive;
    private boolean isNext;
    private boolean isLast;
}
