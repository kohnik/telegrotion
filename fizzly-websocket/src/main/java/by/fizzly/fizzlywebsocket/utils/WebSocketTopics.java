package by.fizzly.fizzlywebsocket.utils;

public class WebSocketTopics {

    private static final String TOPIC_PREFIX = "/topic";

    public static final String BRAIN_RING = "/brain-ring";

    public static final String JOIN_QUIZ_TOPIC = TOPIC_PREFIX + "/quiz/%s";

    public static final String BRAIN_RING_TOPIC_PREFIX = TOPIC_PREFIX + BRAIN_RING;

    public static final String JOIN_BRAIN_RING_TOPIC = BRAIN_RING_TOPIC_PREFIX + "/%s";

}
