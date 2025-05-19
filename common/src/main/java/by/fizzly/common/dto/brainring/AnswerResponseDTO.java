package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "DTO для ответа игрока BrainRing")
public class AnswerResponseDTO {
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Имя игрока")
    private String playerName;
    
    @Schema(description = "Время ответа игрока")
    private double answerTime;
}
