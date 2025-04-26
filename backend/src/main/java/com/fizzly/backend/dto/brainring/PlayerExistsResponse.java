package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PlayerExistsResponse {
    private boolean exists;
    private String teamName;
    private UUID playerId;
    private UUID roomId;
}
