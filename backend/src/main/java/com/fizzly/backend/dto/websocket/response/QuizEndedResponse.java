package com.fizzly.backend.dto.websocket.response;

import com.fizzly.backend.dto.websocket.QuestionEndedPlayerDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class QuizEndedResponse {
    private Long eventId;
    private List<QuestionEndedPlayerDTO> players;
}
