package by.fizzly.common.dto.quiz;

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
@Schema(description = "DTO для ответа, отправленного игроком")
public class PlayerSubmittedAnswer implements Serializable {
    @Schema(description = "Идентификатор игрока")
    private UUID playerId;
    
    @Schema(description = "Ответ игрока (индекс/номер)")
    private int answer;
}
