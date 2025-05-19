package by.fizzly.common.dto.websocket.response;

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
@Schema(description = "DTO для ответа о завершении сессии BrainRing")
public class BrainRingSessionEndedResponse {
    @Schema(description = "Идентификатор события")
    private Long eventId;

    @Schema(description = "ID комнаты")
    private UUID roomId;
}
