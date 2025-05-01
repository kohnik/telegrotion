package by.fizzly.common.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeletePlayerRequestDTO {
    private UUID playerId;
    private UUID roomId;
}