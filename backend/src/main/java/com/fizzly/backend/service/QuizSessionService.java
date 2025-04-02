package com.fizzly.backend.service;

import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizSession;
import com.fizzly.backend.entity.SessionParticipant;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.repository.QuizSessionRepository;
import com.fizzly.backend.repository.SessionParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSessionService {

    private final Map<String, QuizSession> activeSessions = new ConcurrentHashMap<>();

    private final QuizSessionRepository quizSessionRepository;
    private final SessionParticipantRepository sessionParticipantRepository;
    private final QuizService quizService;

    public QuizSession startQuiz(Long quizId, Long userId) {
        Quiz quiz = quizService.findByQuizId(quizId);

        String joinCode = generateJoinCode();
        QuizSession quizSession = new QuizSession();
        quizSession.setActive(true);
        quizSession.setJoinCode(joinCode);
        quizSession.setQuizId(quiz.getId());
        quizSession.setOwnerId(userId);

        QuizSession savedSessionQuiz = quizSessionRepository.save(quizSession);
        activeSessions.put(joinCode, savedSessionQuiz);

        return savedSessionQuiz;
    }

    public void addParticipant(String joinCode, String username) {
        QuizSession session = getSession(joinCode);
        if (session == null) {
            throw new TelegrotionException("Invalid join code");
        }

        SessionParticipant participant = new SessionParticipant();
        participant.setUsername(username);
        participant.setSession(session);
        sessionParticipantRepository.save(participant);

        session.getParticipants().add(participant);
    }

    private String generateJoinCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public QuizSession getSession(String joinCode) {
        return activeSessions.get(joinCode);
    }

}
