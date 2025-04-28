package com.fizzly.backend.service.quiz;

import com.fizzly.backend.converter.QuizConverter;
import com.fizzly.backend.dto.quiz.FullQuizGetDTO;
import com.fizzly.backend.dto.quiz.QuizSessionAnswerDTO;
import com.fizzly.backend.dto.quiz.QuizSessionDTO;
import com.fizzly.backend.dto.websocket.QuestionEndedPlayerDTO;
import com.fizzly.backend.dto.websocket.response.QuestionEndedResponse;
import com.fizzly.backend.dto.websocket.response.QuizEndedResponse;
import com.fizzly.backend.entity.Quiz;
import com.fizzly.backend.entity.QuizEvent;
import com.fizzly.backend.entity.quiz.session.QuizSessionRoom;
import com.fizzly.backend.exception.FizzlyAccessDeniedException;
import com.fizzly.backend.exception.InvalidJoinCodeException;
import com.fizzly.backend.exception.RoomNotFoundException;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.exception.UserAlreadyExistsException;
import com.fizzly.backend.exception.UserNotFoundException;
import com.fizzly.backend.utils.JoinCodeUtils;
import com.fizzly.backend.utils.WebSocketTopics;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSessionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuizSessionService.class);

    private static final String ACTIVE_SESSIONS_KEY = "quiz:sessions:";
    private static final String ACTIVE_SESSIONS_JOIN_CODE_KEY = "quiz:sessions:joinCode:";
    private static final String SESSION_PARTICIPANTS_KEY = "quiz:sessions:participants:";
    private static final String ACTIVE_QUESTIONS_KEY = "quiz:questions:rooms:";
    private static final String SESSION_PARTICIPANTS_WITH_RESULTS_KEY = "quiz:participants:results:";
    private static final String ACTIVE_QUESTION_KEY = "quiz:question:room:";
    private static final String SUBMITTED_ANSWERS_KEY = "quiz:answers:submitted:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizService quizService;
    private final FullQuizService fullQuizService;
    private final SimpMessagingTemplate messagingTemplate;

    public QuizSessionRoom startQuiz(Long quizId, Long userId) {
        LOGGER.info("Attempt to start quiz with id {}", quizId);
        Quiz quiz = quizService.findByQuizId(quizId);

        boolean isUnique = false;
        String joinCode = null;
        while (!isUnique) {
            joinCode = JoinCodeUtils.generateJoinCode();
            isUnique = !redisTemplate.opsForHash().hasKey(ACTIVE_SESSIONS_KEY, joinCode);
        }

        QuizSessionRoom room = new QuizSessionRoom();
        room.setActive(false);
        room.setJoinCode(joinCode);
        room.setRoomId(UUID.randomUUID());
        room.setQuizId(quiz.getId());

        redisTemplate.opsForHash().put(ACTIVE_SESSIONS_KEY, room.getRoomId(), room);
        redisTemplate.opsForValue().set(ACTIVE_SESSIONS_JOIN_CODE_KEY + joinCode, room.getRoomId());
        LOGGER.info("Quiz with id {} joinCode {} and roomId {} was created", quizId, joinCode, room.getRoomId());

        return room;
    }

    private QuizSessionRoom getSessionRoom(UUID roomId) {
        return (QuizSessionRoom) redisTemplate.opsForHash().get(ACTIVE_SESSIONS_KEY, roomId);
    }

    private UUID getSessionRoomIdByJoinCode(String joinCode) {
        Object roomId = redisTemplate.opsForValue().get(ACTIVE_SESSIONS_JOIN_CODE_KEY + joinCode);
        if (roomId == null) {
            throw new InvalidJoinCodeException();
        }
        return UUID.fromString((String) roomId);
    }

    public QuizSessionRoom getSessionRoomByJoinCode(String joinCode) {
        UUID roomId = getSessionRoomIdByJoinCode(joinCode);
        QuizSessionRoom room = getSessionRoom(roomId);
        if (room == null) {
            throw new RoomNotFoundException(roomId);
        }
        return room;
    }

    public void joinParticipant(String joinCode, String username) {
        QuizSessionRoom room = getSessionRoomByJoinCode(joinCode);

        boolean memberExists = redisTemplate.opsForSet().isMember(SESSION_PARTICIPANTS_KEY + room, username);
        if (memberExists) {
            throw new UserAlreadyExistsException(username, room.getRoomId());
        }

        redisTemplate.opsForSet().add(SESSION_PARTICIPANTS_KEY + room.getRoomId(), username);
        redisTemplate.opsForSet().add(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + room.getRoomId(),
                new QuestionEndedPlayerDTO(username, 0));
        LOGGER.info("Player with username {} joined to session with roomId {}", username, room.getRoomId());
    }

    public Set<String> getAllUsersByRoomId(UUID roomId) {
        Set<Object> members = redisTemplate.opsForSet().members(SESSION_PARTICIPANTS_KEY + roomId);
        if (members == null) {
            return Collections.emptySet();
        }
        return members.stream()
                .map(String.class::cast)
                .collect(Collectors.toSet());
    }

    @Transactional
    public int activateSessionRoom(String joinCode, UUID roomId) {
        LOGGER.info("Activation session room {}", roomId);
        QuizSessionRoom room = getSessionRoomByJoinCode(joinCode);
        if (!room.getRoomId().equals(roomId)) {
            throw new FizzlyAccessDeniedException("activateSession");
        }
        room.setActive(true);

        final FullQuizGetDTO fullQuiz = fullQuizService.getFullQuiz(room.getQuizId());
        final List<QuizSessionDTO> questions = fullQuiz.getQuestions().stream()
                .map(QuizConverter::convertToQuizSessionDTO)
                .toList();
        if (questions.isEmpty()) {
            throw new TelegrotionException(String.format("У квиза с id %s отсутствуют вопросы", room.getQuizId()));
        }

        questions.getFirst().setNext(true);
        questions.forEach(question -> redisTemplate.opsForList().leftPush(ACTIVE_QUESTIONS_KEY + roomId, question));
        LOGGER.info("session room {} was activated", roomId);

        return fullQuiz.getQuestions().size();
    }

    public QuizSessionDTO getNextQuestion(UUID roomId) {
        Object questionObj = redisTemplate.opsForList().rightPop(ACTIVE_QUESTIONS_KEY + roomId);
        if (questionObj == null) {
            return null;
        }
        QuizSessionDTO nextQuestion = (QuizSessionDTO) questionObj;
        redisTemplate.opsForValue().set(ACTIVE_QUESTION_KEY + roomId, nextQuestion);
        return nextQuestion;
    }

    public List<String> submitAnswer(String username, int answerOrder, double answerTime, UUID roomId) {
        Object questionObj = redisTemplate.opsForValue().get(ACTIVE_QUESTION_KEY + roomId);
        if (questionObj == null) {
            LOGGER.warn("INVALID JOIN CODE");
            throw new TelegrotionException("Invalid join code");
        }

        final QuizSessionDTO activeQuestion = (QuizSessionDTO) questionObj;

        QuizSessionAnswerDTO correctAnswer = activeQuestion.getAnswers().stream()
                .filter(QuizSessionAnswerDTO::isCorrect)
                .findFirst()
                .orElseThrow(() -> new TelegrotionException(
                        String.format("Не найден правильный ответ на вопрос: %d", activeQuestion.getQuestionId())
                ));

        redisTemplate.opsForSet().add(SUBMITTED_ANSWERS_KEY + roomId + activeQuestion.getQuestionId(), username);
        LOGGER.info("User {} in session room submit answer", username);

        if (correctAnswer.getOrder() == answerOrder) {
            LOGGER.info("User {} made right choise", username);
            Set<Object> members = redisTemplate.opsForSet().members(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + roomId);
            if (members == null) {
                throw new TelegrotionException("Игроки для комнаты не найдены: " + roomId);
            }
            List<QuestionEndedPlayerDTO> players = members.stream()
                    .map(QuestionEndedPlayerDTO.class::cast)
                    .toList();
            QuestionEndedPlayerDTO submittedPlayer = players.stream()
                    .filter(player -> player.getPlayerName().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(username));
            redisTemplate.opsForSet().remove(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + roomId, submittedPlayer);
            submittedPlayer.setPoints(
                    submittedPlayer.getPoints() +
                            calcUserPoints(answerTime, activeQuestion.getTimeLeft(), activeQuestion.getPoints())
            );
            redisTemplate.opsForSet().add(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + roomId, submittedPlayer);
        }

        return getLeftUsers(roomId, activeQuestion.getQuestionId());
    }

    private List<String> getLeftUsers(UUID roomId, Long questionId) {
        Set<String> submittedUsers = redisTemplate.opsForSet()
                .members(SUBMITTED_ANSWERS_KEY + roomId + questionId)
                .stream()
                .map(String.class::cast)
                .collect(Collectors.toSet());

        Set<String> allParticipants = redisTemplate.opsForSet()
                .members(SESSION_PARTICIPANTS_KEY + roomId)
                .stream()
                .map(String.class::cast)
                .collect(Collectors.toSet());

        if (allParticipants == null) {
            return Collections.emptyList();
        }

        return allParticipants.stream()
                .filter(participant -> !submittedUsers.contains(participant))
                .toList();
    }

    private int calcUserPoints(double answerTime, int questionTime, int questionPoints) {
        double v = answerTime / questionTime;
        double k = 1 - v;
        double points = questionPoints * k;
        return (int) points;
    }

    @Transactional
    public void endQuiz(String joinCode, UUID roomId) {
        redisTemplate.delete(ACTIVE_QUESTIONS_KEY + roomId);
        redisTemplate.opsForHash().delete(ACTIVE_SESSIONS_KEY, roomId);

        redisTemplate.delete(SESSION_PARTICIPANTS_KEY + joinCode);

        LOGGER.info("Session room was closed {}", joinCode);
    }

    public void validateJoinCode(String joinCode) {
        getSessionRoomIdByJoinCode(joinCode);
    }

    private List<QuestionEndedPlayerDTO> getSubmittedAnswerPlayers(UUID roomId) {
        Set<Object> members = redisTemplate.opsForSet().members(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + roomId);
        if (members == null || members.isEmpty()) {
            LOGGER.warn("No users found for room with id: " + roomId);
            return Collections.emptyList();
        }
        return members.stream()
                .map(QuestionEndedPlayerDTO.class::cast)
                .sorted(Comparator.comparingInt(QuestionEndedPlayerDTO::getPoints).reversed())
                .toList();
    }

    public void nextQuestion(String joinCode, UUID roomId) {
        final String topic = String.format(WebSocketTopics.JOIN_TOPIC, roomId);
        List<QuestionEndedPlayerDTO> players = getSubmittedAnswerPlayers(roomId);

        QuizSessionDTO question = getNextQuestion(roomId);
        if (question == null) {
            messagingTemplate.convertAndSend(topic, new QuizEndedResponse(QuizEvent.QUIZ_FINISHED.getId(), players));
            endQuiz(joinCode, roomId);
            return;
        }

        messagingTemplate.convertAndSend(topic, question);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            final int seconds = question.getTimeLeft();
            try {
                for (int i = 0; i < seconds; i++) {
                    TimeUnit.SECONDS.sleep(1);
                    if (getLeftUsers(roomId, question.getQuestionId()).isEmpty()) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (question.isLast()) {
                messagingTemplate.convertAndSend(topic, new QuizEndedResponse(QuizEvent.QUIZ_FINISHED.getId(), getSubmittedAnswerPlayers(roomId)));
                endQuiz(joinCode, roomId);
                return;
            }
            int order = question.getAnswers().stream()
                    .filter(QuizSessionAnswerDTO::isCorrect)
                    .findFirst().orElseThrow(() -> new TelegrotionException(
                            String.format("Не найден правильный ответ на вопрос: %s", question.getQuestionId()))
                    )
                    .getOrder();

            messagingTemplate.convertAndSend(topic,
                    new QuestionEndedResponse(QuizEvent.QUESTION_ENDED.getId(), order, getSubmittedAnswerPlayers(roomId))
            );
            LOGGER.info("Question in session room {} was ended", joinCode);
        });
        executorService.shutdown();
    }
}
