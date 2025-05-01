package by.fizzly.common.dto.brainring;

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
public class BrainRingPlayer implements Serializable {
    private UUID playerId;
    private String playerName;
}
