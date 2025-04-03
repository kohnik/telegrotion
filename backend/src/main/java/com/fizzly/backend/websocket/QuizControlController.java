package com.fizzly.backend.websocket;

import com.fizzly.backend.service.QuizSessionService;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class QuizControlController {

    private final QuizSessionService quizSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send-message")
    public void test(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("message = " + message);
    }

    @MessageMapping("/start")
    public void startQuizSession(@Payload StartSessionRequest request, SimpMessageHeaderAccessor headerAccessor) {
        final String joinCode = request.joinCode;
        int questionsCount = quizSessionService.activateSession(joinCode);

        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, joinCode);
        messagingTemplate.convertAndSend(topic, new StartSessionResponse(
                "quizStarted",
                joinCode,
                questionsCount)
        );
    }

    @MessageMapping("/nextQuestion")
    public void nextQuestion(@Payload NextQuestionRequest request, SimpMessageHeaderAccessor headerAccessor) {
        final String joinCode = request.joinCode;
        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, joinCode);

        QuizSessionService.QuizSessionDTO question = quizSessionService.nextQuestion(joinCode);
        if (question == null) {
            messagingTemplate.convertAndSend(topic, "quizFinished");
            return;
        }

        messagingTemplate.convertAndSend(topic, question);

        final int seconds = question.getTimeLeft();
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        quizSessionService.endQuestion(joinCode);
        messagingTemplate.convertAndSend(topic,
                new QuestionEndedResponse("questionEnded", question.getAnswers().stream()
                        .filter(answer -> answer.isCorrect())
                        .findFirst().get()
                        .getOrder())
        );
    }

    @MessageMapping("/submit-answer")
    public void submitAnswer(@Payload SubmitAnswerRequest request, SimpMessageHeaderAccessor headerAccessor) {
        quizSessionService.submitAnswer(request.joinCode, request.username, request.getAnswer());

        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, request.getJoinCode());
        messagingTemplate.convertAndSend(topic, new SubmitAnswerResponse("answerSubmitted", request.getUsername()));
    }

    @Getter
    @Setter
    private static class StartSessionRequest {
        private String joinCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class StartSessionResponse {
        private String event;
        private String joinCode;
        private int questionCount;
    }

    @Getter
    @Setter
    private static class NextQuestionRequest {
        private String joinCode;
    }

    @Getter
    @Setter
    private static class SubmitAnswerRequest {
        private String joinCode;
        private String username;
        private int answer;
        private double timeSpent;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class SubmitAnswerResponse {
        private String event;
        private String username;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class QuestionEndedResponse {
        private String event;
        private int correctAnswer;
    }
}
