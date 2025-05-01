package by.fizzly.common.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrainRingRoomFullDTO {
    private UUID roomId;
    private String joinCode;
    private List<BrainRingPlayer> players;
}