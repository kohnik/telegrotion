package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AnswerResponseWithEventDTO extends AnswerResponseDTO {

    private Long eventId;

    public AnswerResponseWithEventDTO(Long eventId, UUID playerId, String teamName, double answerTime) {
        super(playerId, teamName, answerTime);
        this.eventId = eventId;
    }
}
