package com.fizzly.backend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionEndedPlayerDTO {
    private String playerName;
    private int points;
}
