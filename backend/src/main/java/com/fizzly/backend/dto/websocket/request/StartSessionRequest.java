package com.fizzly.backend.dto.websocket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartSessionRequest {
    private String joinCode;
}
