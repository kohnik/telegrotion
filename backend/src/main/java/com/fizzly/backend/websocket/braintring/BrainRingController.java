package com.fizzly.backend.websocket.braintring;

import com.fizzly.backend.entity.BrainRingEvent;
import com.fizzly.backend.service.brainring.BrainRingService;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BrainRingController {

    private final BrainRingService brainRingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/brain-ring/start")
    public void activateRoom(@Payload ActiveRoomRequestDTO requestDTO) {
        BrainRingService.BrainRingActiveRoom room = brainRingService.activateRoom(requestDTO.getRoomId());

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
                requestDTO.roomId, requestDTO.getTeamId(), requestDTO.getAnswerTime()
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
        brainRingService.activateNextQuestionInRoom(requestDTO.roomId);

        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, requestDTO.getRoomId());
        messagingTemplate.convertAndSend(topic, new NextQuestionResponseDTO(
                BrainRingEvent.NEXT_ROUND.getId(),
                requestDTO.getRoomId(),
                true
        ));
    }

    @Getter
    @Setter
    public static class ActiveRoomRequestDTO {
        private UUID roomId;
    }

    @Getter
    @Setter
    public static class AnswerRequestDTO {
        private UUID roomId;
        private UUID teamId;
        private double answerTime;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AnswerResponseDTO {
        private UUID teamId;
        private String teamName;
        private double answerTime;
    }

    @Getter
    @Setter
    public static class AnswerResponseWithEventDTO extends AnswerResponseDTO {

        private Long eventId;

        public AnswerResponseWithEventDTO(Long eventId, UUID teamId, String teamName, double answerTime) {
            super(teamId, teamName, answerTime);
            this.eventId = eventId;
        }
    }

    @Getter
    @Setter
    public static class BrainRingActiveRoomWithEventDTO extends BrainRingService.BrainRingActiveRoom {
        private Long eventId;

        public BrainRingActiveRoomWithEventDTO(boolean ready, String joinCode, List<BrainRingService.BrainRingTeam> teams, Long eventId) {
            super(ready, joinCode, teams);
            this.eventId = eventId;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NextQuestionResponseDTO {
        private Long eventId;
        private UUID roomId;
        private boolean ready;
    }
}
