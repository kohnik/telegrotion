package com.fizzly.backend.dto.websocket.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubmitAnswerRequest {
    private UUID roomId;
    private UUID playerId;
    private int answer;
    private double timeSpent;
}
