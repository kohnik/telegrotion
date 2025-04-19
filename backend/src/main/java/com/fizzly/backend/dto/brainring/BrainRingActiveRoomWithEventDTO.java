package com.fizzly.backend.dto.brainring;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BrainRingActiveRoomWithEventDTO extends BrainRingActiveRoom {
    private Long eventId;

    public BrainRingActiveRoomWithEventDTO(boolean ready, String joinCode, List<BrainRingTeam> teams, Long eventId) {
        super(ready, joinCode, teams);
        this.eventId = eventId;
    }
}
