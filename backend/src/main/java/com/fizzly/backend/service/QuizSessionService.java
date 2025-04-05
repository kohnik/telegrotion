package com.fizzly.backend.service;

import com.fizzly.backend.dto.FullQuizGetDTO;
import com.fizzly.backend.dto.QuizSessionAnswerDTO;
import com.fizzly.backend.dto.QuizSessionDTO;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizSession;
import com.fizzly.backend.entity.SessionParticipant;
import com.fizzly.backend.exception.InvalidJoinCodeException;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.exception.UserNotFoundException;
import com.fizzly.backend.repository.QuizSessionRepository;
import com.fizzly.backend.repository.SessionParticipantRepository;
import com.fizzly.backend.utils.JoinCodeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSessionService {

    private final Map<String, QuizSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<String, List<QuizSessionDTO>> activeQuizQuestions = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> submittedAnswersByQuestion = new ConcurrentHashMap<>();

    private final QuizSessionRepository quizSessionRepository;
    private final SessionParticipantRepository sessionParticipantRepository;
    private final QuizService quizService;
    private final FullQuizService fullQuizService;

    public QuizSession startQuiz(Long quizId, Long userId) {
        Quiz quiz = quizService.findByQuizId(quizId);

        boolean isUnique = false;
        String joinCode = null;
        while (!isUnique) {
            joinCode = JoinCodeUtils.generateJoinCode();
            isUnique = !activeSessions.containsKey(joinCode);
        }
        QuizSession quizSession = new QuizSession();
        quizSession.setActive(false);
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
            throw new InvalidJoinCodeException();
        }

        SessionParticipant participant = new SessionParticipant();
        participant.setUsername(username);
        participant.setSession(session);
        sessionParticipantRepository.save(participant);

        session.getParticipants().add(participant);
    }

    public QuizSession getSession(String joinCode) {
        return activeSessions.get(joinCode);
    }

    @Transactional
    public int activateSession(String joinCode) {
        QuizSession session = activeSessions.get(joinCode);
        if (session == null) {
            throw new InvalidJoinCodeException();
        }
        session.setActive(true);
        quizSessionRepository.save(session);

        final FullQuizGetDTO fullQuiz = fullQuizService.getFullQuiz(session.getQuizId());
        final List<QuizSessionDTO> questions = fullQuiz.getQuestions().stream()
                .map(quizDTO -> {
                    QuizSessionDTO quizSessionDTO = new QuizSessionDTO();
                    quizSessionDTO.setQuestionId(quizDTO.getQuestionId());
                    quizSessionDTO.setQuestionName(quizDTO.getQuestion());
                    quizSessionDTO.setTimeLeft(quizDTO.getSeconds());
                    quizSessionDTO.setPoints(quizDTO.getPoints());
                    quizSessionDTO.setAnswers(quizDTO.getAnswers().stream()
                            .map(answerDTO -> {
                                QuizSessionAnswerDTO quizSessionAnswerDTO = new QuizSessionAnswerDTO();
                                quizSessionAnswerDTO.setAnswer(answerDTO.getAnswer());
                                quizSessionAnswerDTO.setOrder(answerDTO.getOrder());
                                quizSessionAnswerDTO.setCorrect(answerDTO.isCorrect());

                                return quizSessionAnswerDTO;
                            }).toList());

                    return quizSessionDTO;
                }).toList();
        questions.getFirst().setNext(true);
        activeQuizQuestions.put(joinCode, questions);

        return fullQuiz.getQuestions().size();
    }

    public QuizSessionDTO nextQuestion(String joinCode) {
        List<QuizSessionDTO> questions = activeQuizQuestions.get(joinCode);
        if (questions == null) {
            throw new InvalidJoinCodeException();
        }

        //typical order
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isNext()) {
                questions.get(i).setActive(true);
                questions.get(i).setNext(false);
                questions.get(i).setLast(true);
                if (i + 1 < questions.size()) {
                    questions.get(i).setLast(false);
                    questions.get(i + 1).setNext(true);
                }
                return questions.get(i);
            }
        }
        return null;
    }

    public void endQuestion(String joinCode) {
        List<QuizSessionDTO> questions = activeQuizQuestions.get(joinCode);
        if (questions == null) {
            throw new InvalidJoinCodeException();
        }

        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isActive()) {
                questions.get(i).setActive(false);
            }
        }
    }

    public List<String> submitAnswer(String joinCode, String username, int answerOrder, double answerTime) {
        List<QuizSessionDTO> questions = activeQuizQuestions.get(joinCode);
        if (questions == null) {
            throw new TelegrotionException("Invalid join code");
        }

        final QuizSessionDTO activeQuestion = questions.stream().filter(QuizSessionDTO::isActive).findFirst()
                .orElseThrow(() -> new TelegrotionException("Не найдена активная сессия"));
        QuizSessionAnswerDTO correctAnswer = activeQuestion.getAnswers().stream().filter(QuizSessionAnswerDTO::isCorrect)
                .findFirst()
                .orElseThrow(() -> new TelegrotionException(
                        (String.format("Не найден правильный ответ на вопрос: %d", activeQuestion.getQuestionId()))
                ));

        Set<String> usersByQuestion = submittedAnswersByQuestion.getOrDefault(activeQuestion.getQuestionId(), new HashSet<>());
        usersByQuestion.add(username);

        QuizSession quizSession = activeSessions.get(joinCode);
        if (correctAnswer.getOrder() == answerOrder) {
            SessionParticipant sessionParticipant = quizSession.getParticipants().stream()
                    .filter(participant -> participant.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(username));
            sessionParticipant.setPoints(
                    sessionParticipant.getPoints() +
                            calcUserPoints(answerTime, activeQuestion.getTimeLeft(), activeQuestion.getPoints())
            );
        }

        List<String> allQuizParticipants = quizSession.getParticipants().stream()
                .map(SessionParticipant::getUsername)
                .collect(Collectors.toList());
        allQuizParticipants.removeAll(usersByQuestion);

        return allQuizParticipants;
    }

    private int calcUserPoints(double answerTime, int questionTime, int questionPoints) {
        return (int) Math.round((1 - answerTime / questionTime) * questionPoints);
    }

    public void endQuiz(String joinCode) {
        activeQuizQuestions.remove(joinCode);
        activeSessions.remove(joinCode);
    }

    public void validateJoinCode(String joinCode) {
        if (activeSessions.get(joinCode) == null) {
            throw new InvalidJoinCodeException();
        }
    }
}
