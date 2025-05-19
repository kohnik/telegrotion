package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "DTO для описания комнаты BrainRing")
public class RoomDescriptionDTO {
    @Schema(description = "Идентификатор события")
    private Long eventId;
    
    @Schema(description = "Имя игрока")
    private String playerName;
    
    @Schema(description = "Код для присоединения к комнате")
    private String joinCode;
    
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Количество игроков в комнате")
    private int playerCount;
}
