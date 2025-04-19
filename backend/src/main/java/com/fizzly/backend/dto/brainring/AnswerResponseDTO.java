package com.fizzly.backend.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AnswerResponseDTO {
    private UUID teamId;
    private String teamName;
    private double answerTime;
}
