package com.fizzly.backend.dto.websocket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {
    private String joinCode;
    private String playerName;
    private int answer;
    private double timeSpent;
}
