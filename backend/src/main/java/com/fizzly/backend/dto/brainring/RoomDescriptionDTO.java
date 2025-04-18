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
    private String teamName;
    private String joinCode;
    private UUID teamId;
    private int teamCount;
}
