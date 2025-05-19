package by.fizzly.fizzlywebsocket.websocket;

import by.fizzly.common.dto.websocket.request.NextQuestionRequest;
import by.fizzly.common.dto.websocket.request.StartSessionRequest;
import by.fizzly.common.dto.websocket.request.SubmitAnswerRequest;
import by.fizzly.common.dto.websocket.response.StartSessionResponse;
import by.fizzly.common.dto.websocket.response.SubmitAnswerResponse;
import by.fizzly.common.event.QuizEvent;
import by.fizzly.fizzlywebsocket.exception.FizzlyAppException;
import by.fizzly.fizzlywebsocket.service.QuizSessionService;
import by.fizzly.fizzlywebsocket.utils.RedisKeys;
import by.fizzly.fizzlywebsocket.utils.WebSocketEndpoints;
import by.fizzly.fizzlywebsocket.utils.WebSocketTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class QuizControlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuizControlController.class);

    public static final String ADMIN_PLAYER_ID = "admin";

    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/quiz/start")
    public void startQuizSession(@Payload StartSessionRequest request) {
        final String joinCode = request.getJoinCode();
        int questionsCount = quizSessionService.activateSessionRoom(joinCode, request.getRoomId());

        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, request.getRoomId());
        StartSessionResponse response = new StartSessionResponse(
                QuizEvent.QUIZ_STARTED.getId(),
                joinCode,
                questionsCount);
        saveEventData(request.getRoomId(), QuizEvent.QUIZ_STARTED, response, ADMIN_PLAYER_ID);
        messagingTemplate.convertAndSend(topic, response);
    }

    @MessageMapping("/quiz/next-question")
    public void nextQuestion(@Payload NextQuestionRequest request) {
        quizSessionService.nextQuestion(request.getJoinCode(), request.getRoomId());
    }

    @MessageMapping("/quiz/submit-answer")
    public void submitAnswer(@Payload SubmitAnswerRequest request) {
        List<String> usersLeft = quizSessionService.submitAnswer(
                request.getPlayerId(),
                request.getAnswer(),
                request.getTimeSpent(),
                request.getRoomId()
        );

        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, request.getRoomId());
        SubmitAnswerResponse submitAnswerResponse = new SubmitAnswerResponse(
                QuizEvent.ANSWER_SUBMITTED.getId(), request.getPlayerId(), usersLeft
        );
        saveEventData(request.getRoomId(), QuizEvent.ANSWER_SUBMITTED, submitAnswerResponse, request.getPlayerId().toString());
        messagingTemplate.convertAndSend(topic, submitAnswerResponse);
    }

    @MessageMapping(WebSocketEndpoints.QUIZ_CURRENT_STATE)
    public void getCurrentState(@Payload PlayerCurrentStateRequest request) {
        UUID playerId = request.getPlayerId();
        UUID roomId = request.getRoomId();
        Object eventObj = redisTemplate.opsForValue()
                .get(RedisKeys.buildKey(RedisKeys.CURRENT_EVENT_STATUS_PREFIX, roomId.toString()));
        QuizEvent event = QuizEvent.valueOf((String) eventObj);
        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, request.getRoomId());
//        final String payload = (String) redisTemplate.opsForValue()
//                .get(RedisKeys.CURRENT_EVENT_PAYLOAD_PREFIX + roomId);
        switch (event) {
            case QUIZ_STARTED, QUIZ_FINISHED, QUESTION_ENDED -> {
                Optional.ofNullable(
                                redisTemplate.opsForValue()
                                        .get(RedisKeys.buildKey(
                                                RedisKeys.CURRENT_EVENT_PAYLOAD_PREFIX, roomId + ADMIN_PLAYER_ID)
                                        )
                        ).map(String.class::cast)
                        .ifPresent(payload -> {
                            LOGGER.info("Payload for topic {} was sent", topic);
                            messagingTemplate.convertAndSendToUser(playerId.toString(), topic, payload);
                        });
            }
            case NEW_QUESTION, ANSWER_SUBMITTED -> {
                if (isRoomAdmin(playerId, roomId)) {
                    Optional.ofNullable(
                                    redisTemplate.opsForValue()
                                            .get(RedisKeys.buildKey(
                                                    RedisKeys.CURRENT_EVENT_PAYLOAD_PREFIX, roomId + ADMIN_PLAYER_ID)
                                            )
                            ).map(String.class::cast)
                            .ifPresent(payload -> {
                                LOGGER.info("Payload for topic {} was sent", topic);
                                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, payload);
                            });
                    return;
                }

                Object eventPlayerObj = redisTemplate.opsForValue()
                        .get(RedisKeys.buildKey(
                                RedisKeys.CURRENT_EVENT_STATUS_PREFIX, roomId.toString() + playerId)
                        );
                if (eventPlayerObj == null) { // return default state of next-question event
                    Optional.ofNullable(
                                    redisTemplate.opsForValue()
                                            .get(RedisKeys.buildKey(
                                                    RedisKeys.CURRENT_EVENT_PAYLOAD_PREFIX, roomId + ADMIN_PLAYER_ID)
                                            )
                            ).map(String.class::cast)
                            .ifPresent(payload -> {
                                LOGGER.info("Payload for topic {} was sent", topic);
                                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, payload);
                            });
                } else { // if player submit his answer as an example
                    Optional.ofNullable(
                                    redisTemplate.opsForValue()
                                            .get(RedisKeys.buildKey(
                                                    RedisKeys.CURRENT_EVENT_PAYLOAD_PREFIX, roomId.toString() + playerId)
                                            )
                            ).map(String.class::cast)
                            .ifPresent(payload -> {
                                LOGGER.info("Payload for topic {} was sent", topic);
                                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, payload);
                            });
                }
            }
            default -> throw new FizzlyAppException("Unknown quiz event type: " + event);
        }
    }

    private void saveEventData(UUID roomId, QuizEvent event, Object payload, String playerId) {
        try {
            redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.CURRENT_EVENT_STATUS_PREFIX, roomId.toString()), event);
            redisTemplate.opsForValue().set(
                    RedisKeys.buildKey(RedisKeys.CURRENT_EVENT_PAYLOAD_PREFIX, roomId + playerId),
                    objectMapper.writeValueAsString(payload)
            );
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private boolean isRoomAdmin(UUID playerId, UUID roomId) {
        return playerId.equals(roomId);
    }

    @Getter
    @Setter
    public static class PlayerCurrentStateRequest {
        private UUID playerId;
        private UUID roomId;
    }
}
