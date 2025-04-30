package by.fizzly.common.dto.websocket.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StartSessionRequest {
    private String joinCode;
    private UUID roomId;
}
