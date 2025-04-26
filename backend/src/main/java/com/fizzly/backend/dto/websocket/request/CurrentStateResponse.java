package com.fizzly.backend.dto.websocket.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStateResponse {
    private Long eventId;
    private Long currentEventId;
    private String payload;
}
