package com.fizzly.backend.dto.brainring;

import com.fizzly.backend.websocket.braintring.BrainRingController;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AnswerResponseWithEventDTO extends AnswerResponseDTO {

    private Long eventId;

    public AnswerResponseWithEventDTO(Long eventId, UUID teamId, String teamName, double answerTime) {
        super(teamId, teamName, answerTime);
        this.eventId = eventId;
    }
}
