package com.fizzly.backend.controller;

import com.fizzly.backend.entity.QuizSession;
import com.fizzly.backend.service.QuizSessionService;
import com.fizzly.backend.utils.WebSocketTopics;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz-session")
@RequiredArgsConstructor
@Tag(name = "Полное управление сессиями квизов", description = "API управления сессиями квизов")
public class QuizSessionController {

    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<QuizSession> startQuizSession(@RequestBody QuizStartDTO quizStartDTO) {
        return ResponseEntity.ok(
                quizSessionService.startQuiz(quizStartDTO.quizId, quizStartDTO.userId)
        );
    }

    @PostMapping("/join")
    public ResponseEntity<Void> joinParticipant(@RequestBody JoinRequest joinRequest) {
        quizSessionService.addParticipant(joinRequest.getJoinCode(), joinRequest.getUsername());
        QuizSession session = quizSessionService.getSession(joinRequest.getJoinCode());

        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, session.getJoinCode());
        final UserJoinResponse response = new UserJoinResponse(session.getParticipants().size(), session.getJoinCode());
        messagingTemplate.convertAndSend(topic, response);
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    private static class QuizStartDTO {
        private Long quizId;
        private Long userId;
    }

    @Getter
    @Setter
    public static class JoinRequest {
        private String joinCode;
        private String username;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class UserJoinResponse {
        private int userCount;
        private String joinCode;
    }
}
