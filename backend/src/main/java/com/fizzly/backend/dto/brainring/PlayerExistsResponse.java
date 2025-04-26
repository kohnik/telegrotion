package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerExistsResponse {
    private boolean exists;
    private String playerName;
    private UUID playerId;
    private UUID roomId;
}
