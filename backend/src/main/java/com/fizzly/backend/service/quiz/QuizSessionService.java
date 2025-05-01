package com.fizzly.backend.service.quiz;

import by.fizzly.common.dto.quiz.FullQuizGetDTO;
import by.fizzly.common.dto.quiz.PlayerJoinedResponse;
import by.fizzly.common.dto.quiz.QuizSessionAnswerDTO;
import by.fizzly.common.dto.quiz.QuizSessionDTO;
import by.fizzly.common.dto.quiz.QuizSessionRoom;
import by.fizzly.common.dto.websocket.QuestionEndedPlayerDTO;
import by.fizzly.common.dto.websocket.response.QuestionEndedResponse;
import by.fizzly.common.dto.websocket.response.QuizEndedResponse;
import by.fizzly.common.dto.websocket.response.UserJoinResponse;
import by.fizzly.common.event.QuizEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fizzly.backend.converter.QuizConverter;
import com.fizzly.backend.entity.Quiz;
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
    private static final String SESSION_PARTICIPANTS_UUID_KEY = "quiz:sessions:participants:uuid";
    private static final String ACTIVE_QUESTIONS_KEY = "quiz:questions:rooms:";
    private static final String SESSION_PARTICIPANTS_WITH_RESULTS_KEY = "quiz:participants:results:";
    private static final String ACTIVE_QUESTION_KEY = "quiz:question:room:";
    private static final String SUBMITTED_ANSWERS_KEY = "quiz:answers:submitted:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizService quizService;
    private final FullQuizService fullQuizService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

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

    public PlayerJoinedResponse joinPlayer(String joinCode, String playerName) {
        QuizSessionRoom room = getSessionRoomByJoinCode(joinCode);

        Boolean memberExists = redisTemplate.opsForSet().isMember(SESSION_PARTICIPANTS_KEY + room.getRoomId(), playerName);
        if (memberExists == Boolean.TRUE) {
            throw new UserAlreadyExistsException(playerName, room.getRoomId());
        }

        UUID playerId = UUID.randomUUID();
        redisTemplate.opsForSet().add(SESSION_PARTICIPANTS_KEY + room.getRoomId(), playerName);
        redisTemplate.opsForSet().add(SESSION_PARTICIPANTS_UUID_KEY + room.getRoomId(), playerId);
        redisTemplate.opsForSet().add(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + room.getRoomId(),
                new QuestionEndedPlayerDTO(playerId, playerName, 0));

        int playerCount = redisTemplate.opsForSet()
                .members(SESSION_PARTICIPANTS_KEY + room.getRoomId()).size();
        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, room.getRoomId());
        final UserJoinResponse response = new UserJoinResponse(playerCount, playerName, joinCode);
        messagingTemplate.convertAndSend(topic, response);

        PlayerJoinedResponse playerResponse = new PlayerJoinedResponse(
                room.getRoomId(),
                joinCode,
                playerId,
                playerName
        );
        LOGGER.info("Player with username {} joined to session with roomId {}", playerName, room.getRoomId());

        return playerResponse;
    }

    public Set<String> getAllUsersByRoomId(UUID roomId) {
        return redisTemplate.opsForSet().members(SESSION_PARTICIPANTS_KEY + roomId).stream()
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

    public List<String> submitAnswer(UUID playerId, int answerOrder, double answerTime, UUID roomId) {
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

        redisTemplate.opsForSet().add(SUBMITTED_ANSWERS_KEY + roomId + activeQuestion.getQuestionId(), playerId);
        LOGGER.info("User with id {} in session room submit answer", playerId);

        if (correctAnswer.getOrder() == answerOrder) {
            LOGGER.info("User with id {} made right chose", playerId);
            Set<Object> members = redisTemplate.opsForSet().members(SESSION_PARTICIPANTS_WITH_RESULTS_KEY + roomId);
            if (members == null) {
                throw new TelegrotionException("Игроки для комнаты не найдены: " + roomId);
            }
            List<QuestionEndedPlayerDTO> players = members.stream()
                    .map(QuestionEndedPlayerDTO.class::cast)
                    .toList();
            QuestionEndedPlayerDTO submittedPlayer = players.stream()
                    .filter(player -> player.getPlayerId().equals(playerId))
                    .findFirst()
                    .orElseThrow(() -> new UserNotFoundException(playerId));
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
        List<QuestionEndedPlayerDTO> allPlayers = getSubmittedAnswerPlayers(roomId);
        List<UUID> playerIds = redisTemplate.opsForSet()
                .members(SUBMITTED_ANSWERS_KEY + roomId + questionId)
                .stream()
                .map(obj -> UUID.fromString((String) obj))
                .toList();
        List<String> submittedUsers = allPlayers.stream()
                .filter(player -> playerIds.contains(player.getPlayerId()))
                .map(QuestionEndedPlayerDTO::getPlayerName)
                .toList();

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
    public void endQuiz(UUID roomId) {
        redisTemplate.delete(ACTIVE_QUESTIONS_KEY + roomId);
        redisTemplate.opsForHash().delete(ACTIVE_SESSIONS_KEY, roomId);

        redisTemplate.delete(SESSION_PARTICIPANTS_KEY + roomId);

        LOGGER.info("Session room was closed {}", roomId);
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

    public void nextQuestion(String joinCode, UUID roomId) throws JsonProcessingException {
        final String topic = String.format(WebSocketTopics.JOIN_QUIZ_TOPIC, roomId);
        List<QuestionEndedPlayerDTO> players = getSubmittedAnswerPlayers(roomId);

        QuizSessionDTO question = getNextQuestion(roomId);
        if (question == null) {
            messagingTemplate.convertAndSend(topic, new QuizEndedResponse(QuizEvent.QUIZ_FINISHED.getId(), players));
            endQuiz(roomId);
            return;
        }

//        saveEventData(roomId, QuizEvent.NEW_QUESTION, question, "");
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
                QuizEndedResponse response = new QuizEndedResponse(
                        QuizEvent.QUIZ_FINISHED.getId(), getSubmittedAnswerPlayers(roomId)
                );
//                try {
//                    saveEventData(roomId, QuizEvent.QUIZ_FINISHED, response, "");
//                } catch (JsonProcessingException e) {
//                    throw new RuntimeException(e);
//                }
                messagingTemplate.convertAndSend(topic, response);
                endQuiz(roomId);
                return;
            }
            int order = question.getAnswers().stream()
                    .filter(QuizSessionAnswerDTO::isCorrect)
                    .findFirst().orElseThrow(() -> new TelegrotionException(
                            String.format("Не найден правильный ответ на вопрос: %s", question.getQuestionId()))
                    )
                    .getOrder();

            QuestionEndedResponse response = new QuestionEndedResponse(
                    QuizEvent.QUESTION_ENDED.getId(), order, getSubmittedAnswerPlayers(roomId)
            );
            messagingTemplate.convertAndSend(topic, response);
//            try {
//                saveEventData(roomId, QuizEvent.QUESTION_ENDED, response, "");
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
            LOGGER.info("Question in session room {} was ended", joinCode);
        });
        executorService.shutdown();
    }

    private static final String CURRENT_EVENT_PREFIX = "events:quiz";
    private static final String CURRENT_EVENT_PAYLOAD_PREFIX = "events:quiz:payload";

//    private void saveEventData(UUID roomId, QuizEvent event, Object payload, String playerId)
//            throws JsonProcessingException {
//        redisTemplate.opsForValue().set(CURRENT_EVENT_PREFIX + roomId.toString(), event);
//        redisTemplate.opsForValue().set(
//                CURRENT_EVENT_PAYLOAD_PREFIX + roomId + playerId,
//                objectMapper.writeValueAsString(payload)
//        );
//    }
}
