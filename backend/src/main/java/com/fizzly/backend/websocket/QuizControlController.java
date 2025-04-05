package com.fizzly.backend.websocket;

import com.fizzly.backend.dto.QuizSessionAnswerDTO;
import com.fizzly.backend.dto.QuizSessionDTO;
import com.fizzly.backend.dto.websocket.QuestionEndedPlayerDTO;
import com.fizzly.backend.dto.websocket.request.NextQuestionRequest;
import com.fizzly.backend.dto.websocket.request.StartSessionRequest;
import com.fizzly.backend.dto.websocket.request.SubmitAnswerRequest;
import com.fizzly.backend.dto.websocket.response.QuestionEndedResponse;
import com.fizzly.backend.dto.websocket.response.QuizEndedResponse;
import com.fizzly.backend.dto.websocket.response.StartSessionResponse;
import com.fizzly.backend.dto.websocket.response.SubmitAnswerResponse;
import com.fizzly.backend.entity.QuizEvent;
import com.fizzly.backend.entity.QuizSession;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.service.QuizSessionService;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        final String joinCode = request.getJoinCode();
        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, joinCode);

        QuizSessionDTO question = quizSessionService.nextQuestion(joinCode);
        if (question == null) {
            final QuizSession session = quizSessionService.getSession(joinCode);
            List<QuestionEndedPlayerDTO> players = session.getParticipants().stream()
                    .map(participant -> new QuestionEndedPlayerDTO(participant.getUsername(), participant.getPoints()))
                    .sorted(Comparator.comparingInt(QuestionEndedPlayerDTO::getPoints))
                    .toList();
            messagingTemplate.convertAndSend(topic, new QuizEndedResponse(QuizEvent.QUIZ_FINISHED.getId(), players));
            quizSessionService.endQuiz(joinCode);
            return;
        }

        messagingTemplate.convertAndSend(topic, question);

        final int seconds = question.getTimeLeft();
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        quizSessionService.endQuestion(joinCode);

        final QuizSession session = quizSessionService.getSession(joinCode);
        List<QuestionEndedPlayerDTO> players = session.getParticipants().stream()
                .map(participant -> new QuestionEndedPlayerDTO(participant.getUsername(), participant.getPoints()))
                .toList();
        int order = question.getAnswers().stream()
                .filter(QuizSessionAnswerDTO::isCorrect)
                .findFirst().orElseThrow(() -> new TelegrotionException(
                        String.format("Не найден правильный ответ на вопрос: %s", question.getQuestionId()))
                )
                .getOrder();

        messagingTemplate.convertAndSend(topic,
                new QuestionEndedResponse(QuizEvent.QUESTION_ENDED.getId(), order, players)
        );
    }

    @MessageMapping("/submit-answer")
    public void submitAnswer(@Payload SubmitAnswerRequest request) {
        List<String> usersLeft = quizSessionService.submitAnswer(request.getJoinCode(), request.getUsername(), request.getAnswer());

        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, request.getJoinCode());
        messagingTemplate.convertAndSend(topic, new SubmitAnswerResponse(
                QuizEvent.ANSWER_SUBMITTED.getId(), request.getUsername(), usersLeft)
        );
    }
}
