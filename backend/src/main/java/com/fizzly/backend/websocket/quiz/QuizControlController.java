package com.fizzly.backend.websocket.quiz;

import com.fizzly.backend.dto.websocket.request.NextQuestionRequest;
import com.fizzly.backend.dto.websocket.request.StartSessionRequest;
import com.fizzly.backend.dto.websocket.request.SubmitAnswerRequest;
import com.fizzly.backend.dto.websocket.response.StartSessionResponse;
import com.fizzly.backend.dto.websocket.response.SubmitAnswerResponse;
import com.fizzly.backend.entity.QuizEvent;
import com.fizzly.backend.service.quiz.QuizSessionService;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuizControlController {

    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/start")
    public void startQuizSession(@Payload StartSessionRequest request) {
        final String joinCode = request.getJoinCode();
        int questionsCount = quizSessionService.activateSession(joinCode);

        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, joinCode);
        messagingTemplate.convertAndSend(topic, new StartSessionResponse(
                QuizEvent.QUIZ_STARTED.getId(),
                joinCode,
                questionsCount)
        );
    }

    @MessageMapping("/nextQuestion")
    public void nextQuestion(@Payload NextQuestionRequest request) {
        quizSessionService.nextQuestion(request.getJoinCode());
    }

    @MessageMapping("/submit-answer")
    public void submitAnswer(@Payload SubmitAnswerRequest request) {
        List<String> usersLeft = quizSessionService.submitAnswer(
                request.getJoinCode(),
                request.getPlayerName(),
                request.getAnswer(),
                request.getTimeSpent()
        );

        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, request.getJoinCode());
        messagingTemplate.convertAndSend(topic, new SubmitAnswerResponse(
                QuizEvent.ANSWER_SUBMITTED.getId(), request.getPlayerName(), usersLeft)
        );

//        if (usersLeft.isEmpty()) {
//            quizSessionService.endQuestion(request.getJoinCode());
//        }
    }
}
