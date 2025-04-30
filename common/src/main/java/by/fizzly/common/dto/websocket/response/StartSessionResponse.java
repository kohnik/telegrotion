package by.fizzly.common.dto.websocket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StartSessionResponse {
    private Long eventId;
    private String joinCode;
    private int questionCount;
}
