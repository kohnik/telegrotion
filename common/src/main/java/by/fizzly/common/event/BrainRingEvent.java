package by.fizzly.common.event;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum BrainRingEvent implements Serializable {

    USER_ADDED(1L, "userAdded"),
    ROOM_ACTIVATED(2L, "roomActivated"),
    ANSWER_SUBMITTED(3L, "answerSubmitted"),
    NEXT_ROUND(4L, "nextRound"),
    CURRENT_EVENT(5L, "currentEvent"),
    SESSION_ENDED(6L, "sessionEnded");

    private final Long id;
    private final String name;

    BrainRingEvent(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
