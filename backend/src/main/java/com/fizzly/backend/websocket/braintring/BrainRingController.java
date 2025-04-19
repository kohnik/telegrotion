package com.fizzly.backend.websocket.braintring;

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
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BrainRingController {

    private final BrainRingService brainRingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/brain-ring/start")
    public void activateRoom(@Payload ActiveRoomRequestDTO requestDTO) {
        BrainRingActiveRoom room = brainRingService.activateRoom(requestDTO.getRoomId());

        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
        messagingTemplate.convertAndSend(topic, new BrainRingActiveRoomWithEventDTO(
                room.isReady(),
                room.getJoinCode(),
                room.getTeams(),
                BrainRingEvent.ROOM_ACTIVATED.getId()
        ));
    }

    @MessageMapping("/brain-ring/submit-answer")
    public void submitAnswer(@Payload AnswerRequestDTO requestDTO) {
        AnswerResponseDTO answer = brainRingService.submitAnswer(
                requestDTO.getRoomId(), requestDTO.getTeamId(), requestDTO.getAnswerTime()
        );
        if (answer != null) {
            String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
            messagingTemplate.convertAndSend(topic, new AnswerResponseWithEventDTO(
                    BrainRingEvent.ANSWER_SUBMITTED.getId(),
                    answer.getTeamId(),
                    answer.getTeamName(),
                    answer.getAnswerTime()
            ));
        }
    }

    @MessageMapping("/brain-ring/nextQuestion")
    public void nextQuestion(@Payload ActiveRoomRequestDTO requestDTO) {
        brainRingService.activateNextQuestionInRoom(requestDTO.getRoomId());

        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
        messagingTemplate.convertAndSend(topic, new NextQuestionResponseDTO(
                BrainRingEvent.NEXT_ROUND.getId(),
                requestDTO.getRoomId(),
                true
        ));
    }

}
