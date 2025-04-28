package com.fizzly.backend.controller.quiz;

import com.fizzly.backend.entity.QuizSession;
import com.fizzly.backend.entity.quiz.session.QuizSessionRoom;
import com.fizzly.backend.service.quiz.QuizSessionService;
import com.fizzly.backend.utils.WebSocketTopics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/quiz-session")
@RequiredArgsConstructor
@Tag(name = "Полное управление сессиями квизов", description = "API управления сессиями квизов")
public class QuizSessionController {

    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/start")
    @Operation(summary = "Создать комнату квиза")
    public ResponseEntity<QuizSessionRoom> startQuizSession(@RequestBody QuizStartDTO quizStartDTO) {
        return ResponseEntity.ok(
                quizSessionService.startQuiz(quizStartDTO.quizId, quizStartDTO.userId)
        );
    }

    @PostMapping("/join")
    @Operation(summary = "Присоединить участника")
    public ResponseEntity<Void> joinParticipant(@RequestBody JoinRequest joinRequest) {
        quizSessionService.joinParticipant(joinRequest.getJoinCode(), joinRequest.getUsername());
//        QuizSession session = quizSessionService.getSession(joinRequest.getJoinCode());

//        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, session.getJoinCode());
//        final UserJoinResponse response = new UserJoinResponse(session.getParticipants().size(), joinRequest.username, session.getJoinCode());
//        messagingTemplate.convertAndSend(topic, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    @Operation(summary = "Валидация кода входа в комнату")
    public ResponseEntity<Void> validateJoinCode(@RequestBody ValidateJoinCodeRequest joinCodeRequest) {
        quizSessionService.validateJoinCode(joinCodeRequest.getJoinCode());
        return ResponseEntity.ok().build();
    }

    @GetMapping("{roomId}/participants")
    @Operation(summary = "Получить участников по текущей сессии")
    public Set<String> getQuizParticipants(@PathVariable UUID roomId) {
        return quizSessionService.getAllUsersByRoomId(roomId);
//        QuizSession session = quizSessionService.getSession(joinCode);
//        List<SessionParticipant> participants = session.getParticipants();
//        return ResponseEntity.ok(new CurrentUserSessionStateResponse(
//                        participants.size(), joinCode,
//                        participants.stream()
//                                .map(SessionParticipant::getUsername)
//                                .toList()
//                )
//        );
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
        private String username;
        private String joinCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class CurrentUserSessionStateResponse {
        private int userCount;
        private String joinCode;
        private List<String> users;
    }

    @Getter
    @Setter
    private static class ValidateJoinCodeRequest {
        private String joinCode;
    }
}
