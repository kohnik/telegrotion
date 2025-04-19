package com.fizzly.backend.dto.brainring;

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
    private String teamName;
    private UUID teamId;
}
