package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO для запроса ответа игрока BrainRing")
public class AnswerRequestDTO {
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
    
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Время ответа игрока")
    private double answerTime;
}
