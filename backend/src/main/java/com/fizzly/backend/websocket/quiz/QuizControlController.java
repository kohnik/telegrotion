package com.fizzly.backend.websocket.quiz;

import by.fizzly.common.dto.quiz.QuizSessionDTO;
import by.fizzly.common.dto.websocket.request.NextQuestionRequest;
import by.fizzly.common.dto.websocket.request.StartSessionRequest;
import by.fizzly.common.dto.websocket.request.SubmitAnswerRequest;
import by.fizzly.common.dto.websocket.response.QuestionEndedResponse;
import by.fizzly.common.dto.websocket.response.QuizEndedResponse;
import by.fizzly.common.dto.websocket.response.StartSessionResponse;
import by.fizzly.common.dto.websocket.response.SubmitAnswerResponse;
import by.fizzly.common.event.QuizEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizzly.backend.service.quiz.QuizSessionService;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class QuizControlController {

    private static final String CURRENT_EVENT_PREFIX = "events:quiz";
    private static final String CURRENT_EVENT_PAYLOAD_PREFIX = "events:quiz:payload";

    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @MessageMapping("/quiz/start")
    public void startQuizSession(@Payload StartSessionRequest request) throws JsonProcessingException {
        final String joinCode = request.getJoinCode();
        int questionsCount = quizSessionService.activateSessionRoom(joinCode, request.getRoomId());

        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, request.getRoomId());
        StartSessionResponse startSessionResponse = new StartSessionResponse(
                QuizEvent.QUIZ_STARTED.getId(),
                joinCode,
                questionsCount);
//        saveEventData(request.getRoomId(), QuizEvent.QUIZ_STARTED, startSessionResponse, "");
        messagingTemplate.convertAndSend(topic, startSessionResponse);
    }

    @MessageMapping("/quiz/next-question")
    public void nextQuestion(@Payload NextQuestionRequest request) throws JsonProcessingException {
        quizSessionService.nextQuestion(request.getJoinCode(), request.getRoomId());
    }

    @MessageMapping("/quiz/submit-answer")
    public void submitAnswer(@Payload SubmitAnswerRequest request) throws JsonProcessingException {
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
//        saveEventData(request.getRoomId(), QuizEvent.ANSWER_SUBMITTED, submitAnswerResponse, request.getPlayerId().toString());
        messagingTemplate.convertAndSend(topic, submitAnswerResponse);
    }

    @MessageMapping("/quiz/current-state")
    public void getCurrentState(@Payload PlayerCurrentStateRequest request) {
        UUID playerId = request.getPlayerId();
        UUID roomId = request.getRoomId();
        Object eventObj = redisTemplate.opsForValue()
                .get(CURRENT_EVENT_PREFIX + roomId);
        QuizEvent event = QuizEvent.valueOf((String) eventObj);
        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, request.getRoomId());
        switch (event) {
            case QUIZ_STARTED -> {
                StartSessionResponse response = (StartSessionResponse) redisTemplate.opsForValue()
                        .get(CURRENT_EVENT_PAYLOAD_PREFIX + roomId);
                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, response);
            }
            case QUIZ_FINISHED -> {
                QuizEndedResponse response = (QuizEndedResponse) redisTemplate.opsForValue()
                        .get(CURRENT_EVENT_PAYLOAD_PREFIX + roomId);
                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, response);
            }
            case QUESTION_ENDED -> {
                QuestionEndedResponse response = (QuestionEndedResponse) redisTemplate.opsForValue()
                        .get(CURRENT_EVENT_PAYLOAD_PREFIX + roomId);
                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, response);
            }
            case NEW_QUESTION -> {
                Object val = redisTemplate.opsForValue().get(CURRENT_EVENT_PREFIX + roomId + playerId);
                if (val == null) {
                    QuizSessionDTO response = (QuizSessionDTO) redisTemplate.opsForValue()
                            .get(CURRENT_EVENT_PAYLOAD_PREFIX + roomId + playerId);
                    messagingTemplate.convertAndSendToUser(playerId.toString(), topic, response);
                    return;
                }
                SubmitAnswerResponse response = (SubmitAnswerResponse) redisTemplate.opsForValue()
                        .get(CURRENT_EVENT_PAYLOAD_PREFIX + roomId + playerId);
                messagingTemplate.convertAndSendToUser(playerId.toString(), topic, response);
            }
        }
    }

//    private void saveEventData(UUID roomId, QuizEvent event, Object payload, String playerId)
//            throws JsonProcessingException {
//        redisTemplate.opsForValue().set(CURRENT_EVENT_PREFIX + roomId + playerId, event);
//        redisTemplate.opsForValue().set(
//                CURRENT_EVENT_PAYLOAD_PREFIX + roomId + playerId,
//                objectMapper.writeValueAsString(payload)
//        );
//    }

    @Getter
    @Setter
    public static class PlayerCurrentStateRequest {
        private UUID playerId;
        private UUID roomId;
    }
}
