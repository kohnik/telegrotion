package com.fizzly.backend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionEndedPlayerDTO implements Serializable {
    private String playerName;
    private int points;
}
