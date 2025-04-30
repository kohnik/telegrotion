package by.fizzly.common.dto.websocket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SubmitAnswerResponse {
    private Long eventId;
    private UUID playerId;
    private List<String> playersLeft;
}
