package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ActiveRoomRequestDTO {
    private UUID roomId;
}
