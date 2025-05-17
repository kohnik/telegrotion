package by.fizzly.common.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для информации об игроке после завершения вопроса")
public class QuestionEndedPlayerDTO implements Serializable {
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Имя игрока")
    private String playerName;
    
    @Schema(description = "Количество очков игрока")
    private int points;
}
