package com.fizzly.backend.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RoomDescriptionDTO {
    private Long eventId;
    private String playerName;
    private String joinCode;
    private UUID playerId;
    private int playerCount;
}
