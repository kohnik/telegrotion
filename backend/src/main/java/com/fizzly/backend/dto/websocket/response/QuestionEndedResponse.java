package com.fizzly.backend.dto.websocket.response;

import com.fizzly.backend.dto.websocket.QuestionEndedPlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class QuestionEndedResponse {
    private Long eventId;
    private int correctAnswer;
    private List<QuestionEndedPlayerDTO> players;
}
