package com.fizzly.backend.utils;

public class WebSocketEndpoints {

    private WebSocketEndpoints() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BR_CURRENT_STATE = WebSocketTopics.BRAIN_RING_TOPIC_PREFIX + "/current-state";
    public static final String BR_NEXT_QUESTION = WebSocketTopics.BRAIN_RING_TOPIC_PREFIX + "/nextQuestion";
    public static final String BR_SUBMIT_ANSWER = WebSocketTopics.BRAIN_RING_TOPIC_PREFIX + "/submit-answer";
    public static final String BR_START = WebSocketTopics.BRAIN_RING_TOPIC_PREFIX + "/start";
}
