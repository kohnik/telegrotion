package by.fizzly.common.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerExistsRequest {
    private UUID roomId;
    private UUID playerId;
}
