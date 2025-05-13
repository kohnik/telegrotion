package by.fizzly.fizzlywebsocket.service;

import by.fizzly.common.dto.converter.QuizConverter;
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
import by.fizzly.fizzlywebsocket.utils.RedisKeys;
import by.fizzly.fizzlywebsocket.exception.FizzlyAppException;
import by.fizzly.fizzlywebsocket.feign.QuizFeignClient;
import by.fizzly.fizzlywebsocket.utils.JoinCodeUtils;
import by.fizzly.fizzlywebsocket.utils.WebSocketTopics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final QuizFeignClient quizFeignClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public QuizSessionRoom startQuiz(Long quizId, Long userId) {
        LOGGER.info("Attempt to start quiz with id {}", quizId);

        boolean isUnique = false;
        String joinCode = null;
        while (!isUnique) {
            joinCode = JoinCodeUtils.generateJoinCode();
            isUnique = !redisTemplate.opsForHash().hasKey(RedisKeys.ACTIVE_SESSIONS_KEY, joinCode);
        }

        QuizSessionRoom room = new QuizSessionRoom();
        room.setActive(false);
        room.setJoinCode(joinCode);
        room.setRoomId(UUID.randomUUID());
        room.setQuizId(quizId);

        redisTemplate.opsForHash().put(RedisKeys.ACTIVE_SESSIONS_KEY, room.getRoomId(), room);
        redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.ACTIVE_SESSIONS_JOIN_CODE_KEY, joinCode), room.getRoomId());
        LOGGER.info("Quiz with id {} joinCode {} and roomId {} was created", quizId, joinCode, room.getRoomId());

        return room;
    }

    private QuizSessionRoom getSessionRoom(UUID roomId) {
        return (QuizSessionRoom) redisTemplate.opsForHash().get(RedisKeys.ACTIVE_SESSIONS_KEY, roomId);
    }

    private UUID getSessionRoomIdByJoinCode(String joinCode) {
        Object roomId = redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.ACTIVE_SESSIONS_JOIN_CODE_KEY, joinCode));
        if (roomId == null) {
            throw new FizzlyAppException();
        }
        return UUID.fromString((String) roomId);
    }

    public QuizSessionRoom getSessionRoomByJoinCode(String joinCode) {
        UUID roomId = getSessionRoomIdByJoinCode(joinCode);
        QuizSessionRoom room = getSessionRoom(roomId);
        if (room == null) {
            throw new FizzlyAppException(roomId.toString());
        }
        return room;
    }

    public PlayerJoinedResponse joinPlayer(String joinCode, String playerName) {
        QuizSessionRoom room = getSessionRoomByJoinCode(joinCode);

        Boolean memberExists = redisTemplate.opsForSet().isMember(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_KEY, room.getRoomId().toString()), playerName);
        if (memberExists == Boolean.TRUE) {
            throw new FizzlyAppException(playerName);
        }

        UUID playerId = UUID.randomUUID();
        redisTemplate.opsForSet().add(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_KEY, room.getRoomId().toString()), playerName);
        redisTemplate.opsForSet().add(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_UUID_KEY, room.getRoomId().toString()), playerId);
        redisTemplate.opsForSet().add(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_WITH_RESULTS_KEY, room.getRoomId().toString()),
                new QuestionEndedPlayerDTO(playerId, playerName, 0));

        int playerCount = redisTemplate.opsForSet()
                .members(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_KEY, room.getRoomId().toString())).size();
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
        return redisTemplate.opsForSet().members(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_KEY, roomId.toString())).stream()
                .map(String.class::cast)
                .collect(Collectors.toSet());
    }

    @Transactional
    public int activateSessionRoom(String joinCode, UUID roomId) {
        LOGGER.info("Activation session room {}", roomId);
        QuizSessionRoom room = getSessionRoomByJoinCode(joinCode);
        if (!room.getRoomId().equals(roomId)) {
            throw new FizzlyAppException("activateSession");
        }
        room.setActive(true);

        final FullQuizGetDTO fullQuiz = quizFeignClient.getFullQuizById(room.getQuizId());
        final List<QuizSessionDTO> questions = fullQuiz.getQuestions().stream()
                .map(QuizConverter::convertToQuizSessionDTO)
                .toList();
        if (questions.isEmpty()) {
            throw new FizzlyAppException(String.format("У квиза с id %s отсутствуют вопросы", room.getQuizId()));
        }

        questions.getFirst().setNext(true);
        questions.forEach(question -> redisTemplate.opsForList().leftPush(RedisKeys.buildKey(RedisKeys.ACTIVE_QUESTIONS_KEY, roomId.toString()), question));
        LOGGER.info("session room {} was activated", roomId);

        return fullQuiz.getQuestions().size();
    }

    public QuizSessionDTO getNextQuestion(UUID roomId) {
        Object questionObj = redisTemplate.opsForList().rightPop(RedisKeys.buildKey(RedisKeys.ACTIVE_QUESTIONS_KEY, roomId.toString()));
        if (questionObj == null) {
            return null;
        }
        QuizSessionDTO nextQuestion = (QuizSessionDTO) questionObj;
        redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.ACTIVE_QUESTION_KEY, roomId.toString()), nextQuestion);
        return nextQuestion;
    }

    public List<String> submitAnswer(UUID playerId, int answerOrder, double answerTime, UUID roomId) {
        Object questionObj = redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.ACTIVE_QUESTION_KEY, roomId.toString()));
        if (questionObj == null) {
            LOGGER.warn("INVALID JOIN CODE");
            throw new FizzlyAppException("Invalid join code");
        }

        final QuizSessionDTO activeQuestion = (QuizSessionDTO) questionObj;

        QuizSessionAnswerDTO correctAnswer = activeQuestion.getAnswers().stream()
                .filter(QuizSessionAnswerDTO::isCorrect)
                .findFirst()
                .orElseThrow(() -> new FizzlyAppException(
                        String.format("Не найден правильный ответ на вопрос: %d", activeQuestion.getQuestionId())
                ));

        redisTemplate.opsForSet().add(RedisKeys.buildKey(RedisKeys.SUBMITTED_ANSWERS_KEY, roomId.toString() + activeQuestion.getQuestionId()), playerId);
        LOGGER.info("User with id {} in session room submit answer", playerId);

        if (correctAnswer.getOrder() == answerOrder) {
            LOGGER.info("User with id {} made right chose", playerId);
            Set<Object> members = redisTemplate.opsForSet().members(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_WITH_RESULTS_KEY, roomId.toString()));
            if (members == null) {
                throw new FizzlyAppException("Игроки для комнаты не найдены: " + roomId);
            }
            List<QuestionEndedPlayerDTO> players = members.stream()
                    .map(QuestionEndedPlayerDTO.class::cast)
                    .toList();
            QuestionEndedPlayerDTO submittedPlayer = players.stream()
                    .filter(player -> player.getPlayerId().equals(playerId))
                    .findFirst()
                    .orElseThrow(() -> new FizzlyAppException(playerId.toString()));
            redisTemplate.opsForSet().remove(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_WITH_RESULTS_KEY, roomId.toString()), submittedPlayer);
            submittedPlayer.setPoints(
                    submittedPlayer.getPoints() +
                            calcUserPoints(answerTime, activeQuestion.getTimeLeft(), activeQuestion.getPoints())
            );
            redisTemplate.opsForSet().add(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_WITH_RESULTS_KEY, roomId.toString()), submittedPlayer);
        }

        return getLeftUsers(roomId, activeQuestion.getQuestionId());
    }

    private List<String> getLeftUsers(UUID roomId, Long questionId) {
        List<QuestionEndedPlayerDTO> allPlayers = getSubmittedAnswerPlayers(roomId);
        List<UUID> playerIds = redisTemplate.opsForSet()
                .members(RedisKeys.buildKey(RedisKeys.SUBMITTED_ANSWERS_KEY, roomId.toString() + questionId))
                .stream()
                .map(obj -> UUID.fromString((String) obj))
                .toList();
        List<String> submittedUsers = allPlayers.stream()
                .filter(player -> playerIds.contains(player.getPlayerId()))
                .map(QuestionEndedPlayerDTO::getPlayerName)
                .toList();

        Set<String> allParticipants = redisTemplate.opsForSet()
                .members(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_KEY, roomId.toString()))
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
        redisTemplate.delete(RedisKeys.buildKey(RedisKeys.ACTIVE_QUESTIONS_KEY, roomId.toString()));
        redisTemplate.opsForHash().delete(RedisKeys.ACTIVE_SESSIONS_KEY, roomId);

        redisTemplate.delete(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_KEY, roomId.toString()));

        LOGGER.info("Session room was closed {}", roomId);
    }

    public void validateJoinCode(String joinCode) {
        getSessionRoomIdByJoinCode(joinCode);
    }

    private List<QuestionEndedPlayerDTO> getSubmittedAnswerPlayers(UUID roomId) {
        Set<Object> members = redisTemplate.opsForSet().members(RedisKeys.buildKey(RedisKeys.SESSION_PARTICIPANTS_WITH_RESULTS_KEY, roomId.toString()));
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
                    .findFirst().orElseThrow(() -> new FizzlyAppException(
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
