package com.fizzly.backend.websocket.braintring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizzly.backend.dto.brainring.ActiveRoomRequestDTO;
import com.fizzly.backend.dto.brainring.AnswerRequestDTO;
import com.fizzly.backend.dto.brainring.AnswerResponseDTO;
import com.fizzly.backend.dto.brainring.AnswerResponseWithEventDTO;
import com.fizzly.backend.dto.brainring.BrainRingActiveRoom;
import com.fizzly.backend.dto.brainring.BrainRingActiveRoomWithEventDTO;
import com.fizzly.backend.dto.brainring.NextQuestionResponseDTO;
import com.fizzly.backend.entity.BrainRingEvent;
import com.fizzly.backend.service.brainring.BrainRingService;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BrainRingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainRingController.class);

    private final RedisTemplate<UUID, BrainRingEvent> currentEventRedisTemplate;
    private final RedisTemplate<String, String> currentEventPayloadRedisTemplate;
    private final ObjectMapper objectMapper;
    private final BrainRingService brainRingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/brain-ring/start")
    public void activateRoom(@Payload ActiveRoomRequestDTO requestDTO) throws JsonProcessingException {
        BrainRingActiveRoom room = brainRingService.activateRoom(requestDTO.getRoomId());

        BrainRingActiveRoomWithEventDTO brainRing = new BrainRingActiveRoomWithEventDTO(
                room.isReady(),
                room.getJoinCode(),
                room.getTeams(),
                BrainRingEvent.ROOM_ACTIVATED.getId());
        currentEventRedisTemplate.opsForValue().set(requestDTO.getRoomId(), BrainRingEvent.ROOM_ACTIVATED);
        currentEventPayloadRedisTemplate.opsForValue().set(
                "currentEventPayload:" + requestDTO.getRoomId(),
                objectMapper.writeValueAsString(brainRing)
        );

        LOGGER.info("Set %s for room %s", BrainRingEvent.ROOM_ACTIVATED, requestDTO.getRoomId());
        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
        messagingTemplate.convertAndSend(topic, brainRing);
    }

    //TODO: add sessionID to all mappings
    @MessageMapping("/brain-ring/submit-answer")
    public void submitAnswer(@Payload AnswerRequestDTO requestDTO, @Header("simpSessionId") String sessionId) throws JsonProcessingException {
        AnswerResponseDTO answer = brainRingService.submitAnswer(
                requestDTO.getRoomId(), requestDTO.getTeamId(), requestDTO.getAnswerTime()
        );
        if (answer != null) {
            AnswerResponseWithEventDTO answerResponse = new AnswerResponseWithEventDTO(
                    BrainRingEvent.ANSWER_SUBMITTED.getId(),
                    answer.getTeamId(),
                    answer.getTeamName(),
                    answer.getAnswerTime()
            );
            currentEventRedisTemplate.opsForValue().set(requestDTO.getRoomId(), BrainRingEvent.ANSWER_SUBMITTED);
            currentEventPayloadRedisTemplate.opsForValue().set(
                    "currentEventPayload:" + requestDTO.getRoomId(),
                    objectMapper.writeValueAsString(answerResponse)
            );
            LOGGER.info("Set %s for room %s", BrainRingEvent.ANSWER_SUBMITTED, requestDTO.getRoomId());
            String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
            messagingTemplate.convertAndSend(topic, answerResponse);
        }
    }

    @MessageMapping("/brain-ring/nextQuestion")
    public void nextQuestion(@Payload ActiveRoomRequestDTO requestDTO) throws JsonProcessingException {
        brainRingService.activateNextQuestionInRoom(requestDTO.getRoomId());

        NextQuestionResponseDTO nextQuestionResponseDTO = new NextQuestionResponseDTO(
                BrainRingEvent.NEXT_ROUND.getId(),
                requestDTO.getRoomId(),
                true
        );
        currentEventRedisTemplate.opsForValue().set(requestDTO.getRoomId(), BrainRingEvent.NEXT_ROUND);
        currentEventPayloadRedisTemplate.opsForValue().set(
                "currentEventPayload:" + requestDTO.getRoomId(),
                objectMapper.writeValueAsString(nextQuestionResponseDTO)
        );
        LOGGER.info("Set %s for room %s", BrainRingEvent.NEXT_ROUND, requestDTO.getRoomId());
        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
        messagingTemplate.convertAndSend(topic, nextQuestionResponseDTO);
    }

    @MessageMapping("/brain-ring/current-state")
    public void currentState(@Payload CurrentStateRequest request) {
        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, request.getRoomId());
        String brainRingEventStr = String.valueOf(currentEventRedisTemplate
                .opsForValue().get(request.getRoomId()));
        BrainRingEvent brainRingEvent = BrainRingEvent.valueOf(brainRingEventStr);
        messagingTemplate.convertAndSend(topic, new CurrentStateResponse(
                BrainRingEvent.CURRENT_EVENT.getId(),
                request.getPlayerId(),
                brainRingEvent != null ? brainRingEvent.getId() : -1,
                currentEventPayloadRedisTemplate.opsForValue().get("currentEventPayload:" + request.getRoomId()))
        );
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrentStateRequest {
        private UUID roomId;
        private UUID playerId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurrentStateResponse {
        private Long eventId;
        private UUID playerId;
        private Long currentEventId;
        private String payload;
    }

}
