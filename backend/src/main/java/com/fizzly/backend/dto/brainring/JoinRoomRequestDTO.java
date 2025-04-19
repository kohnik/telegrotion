package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRoomRequestDTO {
    private String teamName;
    private String joinCode;
}
