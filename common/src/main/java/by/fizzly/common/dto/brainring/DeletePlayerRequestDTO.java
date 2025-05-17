package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO для удаления игрока из комнаты")
public class DeletePlayerRequestDTO {
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
}