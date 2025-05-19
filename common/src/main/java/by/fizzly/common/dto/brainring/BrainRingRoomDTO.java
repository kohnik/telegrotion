package by.fizzly.common.dto.brainring;

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
@Schema(description = "DTO для комнаты BrainRing")
public class BrainRingRoomDTO {
    @Schema(description = "Идентификатор комнаты")
    private UUID roomId;
    
    @Schema(description = "Код для присоединения к комнате")
    private String joinCode;
}
