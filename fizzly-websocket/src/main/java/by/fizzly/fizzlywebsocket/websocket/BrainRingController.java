package by.fizzly.fizzlywebsocket.websocket;

import by.fizzly.common.dto.brainring.ActiveRoomRequestDTO;
import by.fizzly.common.dto.brainring.AnswerRequestDTO;
import by.fizzly.common.dto.brainring.AnswerResponseDTO;
import by.fizzly.common.dto.brainring.AnswerResponseWithEventDTO;
import by.fizzly.common.dto.brainring.BrainRingActiveRoom;
import by.fizzly.common.dto.brainring.BrainRingActiveRoomWithEventDTO;
import by.fizzly.common.dto.brainring.NextQuestionResponseDTO;
import by.fizzly.common.dto.websocket.request.CurrentStateRequest;
import by.fizzly.common.dto.websocket.request.CurrentStateResponse;
import by.fizzly.common.event.BrainRingEvent;
import by.fizzly.fizzlywebsocket.exception.FizzlyAppException;
import by.fizzly.fizzlywebsocket.service.BrainRingService;
import by.fizzly.fizzlywebsocket.utils.WebSocketEndpoints;
import by.fizzly.fizzlywebsocket.utils.WebSocketTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BrainRingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainRingController.class);
    private static final String CURRENT_EVENT_PREFIX = "currentEvent:";
    private static final String CURRENT_EVENT_PAYLOAD_PREFIX = "currentEventPayload:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final BrainRingService brainRingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping(WebSocketEndpoints.BR_START)
    public void activateRoom(@Payload ActiveRoomRequestDTO requestDTO) {
        try {
            BrainRingActiveRoom room = brainRingService.activateRoom(requestDTO.getRoomId());

            BrainRingActiveRoomWithEventDTO response = new BrainRingActiveRoomWithEventDTO(
                    room.isReady(),
                    room.getJoinCode(),
                    room.getPlayers(),
                    BrainRingEvent.ROOM_ACTIVATED.getId());

            saveEventData(requestDTO.getRoomId(), BrainRingEvent.ROOM_ACTIVATED, response);

            sendToRoomTopic(requestDTO.getRoomId(), response);
            LOGGER.info("Room activated: {}", requestDTO.getRoomId());

        } catch (Exception e) {
            LOGGER.error("Room not found: {}", requestDTO.getRoomId(), e);
            throw new FizzlyAppException(e.getMessage(), e);
        }
    }

    @MessageMapping(WebSocketEndpoints.BR_SUBMIT_ANSWER)
    public void submitAnswer(@Payload AnswerRequestDTO requestDTO) {
        try {
            AnswerResponseDTO answer = brainRingService.submitAnswer(
                    requestDTO.getRoomId(),
                    requestDTO.getPlayerId(),
                    requestDTO.getAnswerTime()
            );

            if (answer != null) {
                AnswerResponseWithEventDTO response = new AnswerResponseWithEventDTO(
                        BrainRingEvent.ANSWER_SUBMITTED.getId(),
                        answer.getPlayerId(),
                        answer.getPlayerName(),
                        answer.getAnswerTime());

                saveEventData(requestDTO.getRoomId(), BrainRingEvent.ANSWER_SUBMITTED, response);

                sendToRoomTopic(requestDTO.getRoomId(), response);
                LOGGER.info("Answer submitted by {} in room {}",
                        answer.getPlayerName(), requestDTO.getRoomId());
            }
        } catch (Exception e) {
            LOGGER.error("Error submitting answer in room: {}", requestDTO.getRoomId(), e);
            throw new RuntimeException("Failed to submit answer", e);
        }
    }

    @MessageMapping(WebSocketEndpoints.BR_NEXT_QUESTION)
    public void nextQuestion(@Payload ActiveRoomRequestDTO requestDTO) {
        try {
            brainRingService.activateNextQuestionInRoom(requestDTO.getRoomId());

            NextQuestionResponseDTO response = new NextQuestionResponseDTO(
                    BrainRingEvent.NEXT_ROUND.getId(),
                    requestDTO.getRoomId(),
                    true);

            saveEventData(requestDTO.getRoomId(), BrainRingEvent.NEXT_ROUND, response);

            sendToRoomTopic(requestDTO.getRoomId(), response);
            LOGGER.info("Next question activated in room: {}", requestDTO.getRoomId());

        } catch (Exception e) {
            LOGGER.error("Error activating next question in room: {}", requestDTO.getRoomId(), e);
            throw new RuntimeException("Failed to activate next question", e);
        }
    }

    @MessageMapping(WebSocketEndpoints.BR_CURRENT_STATE)
    public void currentState(@Payload CurrentStateRequest request) {
        try {
            String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, request.getRoomId());

            BrainRingEvent event = BrainRingEvent.valueOf((String) redisTemplate.opsForValue()
                    .get(CURRENT_EVENT_PREFIX + request.getRoomId()));

            String payload = (String) redisTemplate.opsForValue()
                    .get(CURRENT_EVENT_PAYLOAD_PREFIX + request.getRoomId());

            CurrentStateResponse response = new CurrentStateResponse(
                    BrainRingEvent.CURRENT_EVENT.getId(),
                    event != null ? event.getId() : -1,
                    payload);

            messagingTemplate.convertAndSendToUser(
                    request.getPlayerId().toString(),
                    topic,
                    response);

            LOGGER.debug("Current state sent to player {} in room {}",
                    request.getPlayerId(), request.getRoomId());

        } catch (Exception e) {
            LOGGER.error("Error getting current state for room: {}", request.getRoomId(), e);
            throw new RuntimeException("Failed to get current state", e);
        }
    }

    private void saveEventData(UUID roomId, BrainRingEvent event, Object payload)
            throws JsonProcessingException {
        redisTemplate.opsForValue().set(CURRENT_EVENT_PREFIX + roomId, event);
        redisTemplate.opsForValue().set(
                CURRENT_EVENT_PAYLOAD_PREFIX + roomId,
                objectMapper.writeValueAsString(payload)
        );
    }

    private void sendToRoomTopic(UUID roomId, Object message) {
        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, roomId);
        messagingTemplate.convertAndSend(topic, message);
    }

}
