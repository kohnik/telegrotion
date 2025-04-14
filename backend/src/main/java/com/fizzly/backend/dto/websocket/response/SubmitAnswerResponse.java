package com.fizzly.backend.dto.websocket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubmitAnswerResponse {
    private Long eventId;
    private String username;
    private List<String> usersLeft;
}
