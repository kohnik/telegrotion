package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для запроса на присоединение к комнате BrainRing")
public class JoinRoomRequestDTO {
    @Schema(description = "Имя игрока")
    private String playerName;
    
    @Schema(description = "Код для присоединения к комнате")
    private String joinCode;
}
