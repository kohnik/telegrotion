package by.fizzly.common.dto.brainring;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO для активной комнаты BrainRing с событием")
public class BrainRingActiveRoomWithEventDTO extends BrainRingActiveRoom {
    @Schema(description = "Идентификатор события")
    private Long eventId;

    public BrainRingActiveRoomWithEventDTO(boolean ready, String joinCode, List<BrainRingPlayer> teams, Long eventId) {
        super(ready, joinCode, teams);
        this.eventId = eventId;
    }
}
