package com.fizzly.backend.dto.websocket.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStateRequest {
    private UUID roomId;
    private UUID playerId;
}
