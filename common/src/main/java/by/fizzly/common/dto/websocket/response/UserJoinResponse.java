package by.fizzly.common.dto.websocket.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для ответа о присоединении пользователя")
public class UserJoinResponse {
    private int userCount;
    private String username;
    private String joinCode;
}
