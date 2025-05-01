package by.fizzly.common.dto.websocket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserJoinResponse {
    private int userCount;
    private String username;
    private String joinCode;
}
