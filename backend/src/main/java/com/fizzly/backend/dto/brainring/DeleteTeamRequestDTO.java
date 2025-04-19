package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteTeamRequestDTO {
    private UUID teamId;
    private UUID roomId;
}