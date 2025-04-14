package com.fizzly.backend.dto.websocket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {
    private String joinCode;
    private String username;
    private int answer;
    private double timeSpent;
}
