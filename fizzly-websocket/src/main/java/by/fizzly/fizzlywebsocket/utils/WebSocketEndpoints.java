package by.fizzly.fizzlywebsocket.utils;

public class WebSocketEndpoints {

    private WebSocketEndpoints() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BR_CURRENT_STATE = WebSocketTopics.BRAIN_RING + "/current-state";
    public static final String BR_NEXT_QUESTION = WebSocketTopics.BRAIN_RING + "/nextQuestion";
    public static final String BR_SUBMIT_ANSWER = WebSocketTopics.BRAIN_RING + "/submit-answer";
    public static final String BR_START = WebSocketTopics.BRAIN_RING + "/start";

    public static final String QUIZ_TOPIC = "/quiz";
    public static final String QUIZ_CURRENT_STATE = QUIZ_TOPIC + "/current-state";
}
