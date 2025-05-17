package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для активной комнаты BrainRing")
public class BrainRingActiveRoom implements Serializable {
    @Schema(description = "Готовность комнаты")
    private boolean ready;
    
    @Schema(description = "Код для присоединения к комнате")
    private String joinCode;
    
    @Schema(description = "Список игроков в комнате")
    private List<BrainRingPlayer> players;
}
