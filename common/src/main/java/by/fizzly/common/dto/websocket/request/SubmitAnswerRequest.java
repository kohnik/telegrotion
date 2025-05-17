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
@Schema(description = "DTO для запроса отправки ответа")
public class SubmitAnswerRequest {
    @Schema(description = "Идентификатор события")
    private Long eventId;
    
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
    
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Ответ игрока")
    private int answer;
    
    @Schema(description = "Время, потраченное на ответ")
    private double timeSpent;
}
