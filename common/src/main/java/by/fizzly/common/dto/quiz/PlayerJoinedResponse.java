package by.fizzly.common.dto.quiz;

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
@Schema(description = "DTO для ответа о присоединении игрока к квизу")
public class PlayerJoinedResponse {
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
    
    @Schema(description = "Код для присоединения к комнате")
    private String joinCode;
    
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Имя игрока")
    private String playerName;
}
