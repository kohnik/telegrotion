package com.fizzly.backend.utils;

public class WebSocketTopics {

    private static final String TOPIC_PREFIX = "/topic";

    public static final String JOIN_TOPIC = TOPIC_PREFIX + "/session/%s";

    public static final String JOIN_BRAIN_RING_TOPIC = TOPIC_PREFIX + "/brainring/%s";
}
