package com.fizzly.backend.entity;

import lombok.Getter;

@Getter
public enum BrainRingEvent {

    USER_ADDED(1L, "userAdded"),
    ROOM_ACTIVATED(2L, "roomActivated"),
    ANSWER_SUBMITTED(3L, "answerSubmitted"),
    NEXT_ROUND(4L, "nextRound");

    private final Long id;
    private final String name;

    BrainRingEvent(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
