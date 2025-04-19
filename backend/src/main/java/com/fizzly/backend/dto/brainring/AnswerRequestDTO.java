package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AnswerRequestDTO {
    private UUID roomId;
    private UUID teamId;
    private double answerTime;
}
