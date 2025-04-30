package by.fizzly.common.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrainRingJoinRoomDTO {
    private UUID roomId;
    private String joinCode;
    private String playerName;
    private UUID playerId;
}
