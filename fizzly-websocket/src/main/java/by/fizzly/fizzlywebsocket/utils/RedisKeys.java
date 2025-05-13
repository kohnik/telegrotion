package by.fizzly.fizzlywebsocket.utils;

public final class RedisKeys {
    private RedisKeys() {
        throw new IllegalStateException("Utility class");
    }

    // Quiz Session related keys
    public static final String ACTIVE_SESSIONS_KEY = "quiz:active_sessions";
    public static final String ACTIVE_SESSIONS_JOIN_CODE_KEY = "quiz:active_sessions:join_code:";
    public static final String SESSION_PARTICIPANTS_KEY = "quiz:session:participants:";
    public static final String SESSION_PARTICIPANTS_UUID_KEY = "quiz:session:participants:uuid:";
    public static final String SESSION_PARTICIPANTS_WITH_RESULTS_KEY = "quiz:session:participants:results:";
    public static final String ACTIVE_QUESTIONS_KEY = "quiz:session:questions:";
    public static final String ACTIVE_QUESTION_KEY = "quiz:session:current_question:";
    public static final String SUBMITTED_ANSWERS_KEY = "quiz:session:submitted_answers:";
    public static final String EVENT_PREFIX = "events:quiz";
    public static final String EVENT_PAYLOAD_PREFIX = "events:quiz:payload";

    // BrainRing related keys
    public static final String BRAINRING_ROOM_KEY = "brainring:room:";
    public static final String BRAINRING_JOIN_CODE_KEY = "brainring:join_code:";
    public static final String BRAINRING_PLAYERS_KEY = "brainring:room:players:";
    public static final String BRAINRING_PLAYER_NAMES_KEY = "brainring:room:players:names:";
    public static final String BRAINRING_ACTIVE_ROOM_KEY = "brainring:active_room:";

    public static String buildKey(String baseKey, String suffix) {
        return baseKey + suffix;
    }
} 