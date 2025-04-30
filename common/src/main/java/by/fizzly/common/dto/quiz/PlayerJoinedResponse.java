package by.fizzly.common.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerJoinedResponse {
    private UUID roomId;
    private String joinCode;
    private UUID playerId;
    private String playerName;
}
