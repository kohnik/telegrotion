package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "DTO для запроса активной комнаты")
public class ActiveRoomRequestDTO {
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
}
