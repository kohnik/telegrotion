package by.fizzly.common.dto.websocket.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для запроса начала сессии")
public class StartSessionRequest {
    @Schema(description = "Идентификатор события")
    private Long eventId;
    private String joinCode;
    private UUID roomId;
}
