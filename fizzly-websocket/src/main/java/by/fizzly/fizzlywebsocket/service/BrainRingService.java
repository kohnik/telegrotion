package by.fizzly.fizzlywebsocket.service;

import by.fizzly.common.dto.brainring.AnswerResponseDTO;
import by.fizzly.common.dto.brainring.BrainRingActiveRoom;
import by.fizzly.common.dto.brainring.BrainRingJoinRoomDTO;
import by.fizzly.common.dto.brainring.BrainRingPlayer;
import by.fizzly.common.dto.brainring.BrainRingRoomDTO;
import by.fizzly.common.dto.brainring.BrainRingRoomFullDTO;
import by.fizzly.common.dto.brainring.PlayerExistsResponse;
import by.fizzly.fizzlywebsocket.utils.RedisKeys;
import by.fizzly.fizzlywebsocket.exception.FizzlyAppException;
import by.fizzly.fizzlywebsocket.utils.JoinCodeUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrainRingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainRingService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public BrainRingRoomDTO createRoom() {
        String joinCode = JoinCodeUtils.generateJoinCode();
        UUID roomId = UUID.randomUUID();

        redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.BRAINRING_ROOM_KEY, roomId.toString()), joinCode);
        redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.BRAINRING_JOIN_CODE_KEY, joinCode), roomId.toString());

        LOGGER.info("Created room: id={}, joinCode={}", roomId, joinCode);
        return new BrainRingRoomDTO(roomId, joinCode);
    }

    public BrainRingJoinRoomDTO joinRoom(String joinCode, String playerName) {
        UUID roomId = getRoomByJoinCode(joinCode);
        if (roomId == null) {
            throw new FizzlyAppException(joinCode);
        }
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYER_NAMES_KEY, roomId.toString()), playerName))) {
            String exMessage = String.format("Игрок с никнеймом %s уже существует", playerName);
            LOGGER.error(exMessage);
            throw new FizzlyAppException(exMessage);
        }

        BrainRingPlayer player = new BrainRingPlayer(UUID.randomUUID(), playerName);

        redisTemplate.opsForHash().put(
                RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYERS_KEY, roomId.toString()),
                player.getPlayerId().toString(),
                player
        );

        redisTemplate.opsForSet().add(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYER_NAMES_KEY, roomId.toString()), playerName);

        LOGGER.info("Player joined: roomId={}, player={}", roomId, playerName);
        return new BrainRingJoinRoomDTO(roomId, joinCode, playerName, player.getPlayerId());
    }

    public void deletePlayer(UUID playerId, UUID roomId) {
        BrainRingPlayer player = (BrainRingPlayer) redisTemplate.opsForHash().get(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYERS_KEY, roomId.toString()), playerId.toString());
        if (player == null) {
            throw new FizzlyAppException(playerId.toString());
        }

        redisTemplate.opsForHash().delete(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYERS_KEY, roomId.toString()), playerId.toString());
        redisTemplate.opsForSet().remove(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYER_NAMES_KEY, roomId.toString()), player.getPlayerName());
    }

    public BrainRingRoomFullDTO getRoomFullInfo(UUID roomId) {
        String joinCode = (String) redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.BRAINRING_ROOM_KEY, roomId.toString()));
        if (joinCode == null) {
            throw new FizzlyAppException(roomId.toString());
        }

        List<BrainRingPlayer> players = redisTemplate.opsForHash()
                .values(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYERS_KEY, roomId.toString()))
                .stream()
                .map(BrainRingPlayer.class::cast)
                .toList();

        return new BrainRingRoomFullDTO(roomId, joinCode, players);
    }

    private UUID getRoomByJoinCode(String joinCode) {
        String roomIdStr = (String) redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.BRAINRING_JOIN_CODE_KEY, joinCode));
        return roomIdStr != null ? UUID.fromString(roomIdStr) : null;
    }

    public BrainRingActiveRoom activateRoom(UUID roomId) {
        String joinCode = (String) redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.BRAINRING_ROOM_KEY, roomId.toString()));
        if (joinCode == null) {
            throw new FizzlyAppException(roomId.toString());
        }

        List<BrainRingPlayer> players = getRoomFullInfo(roomId).getPlayers();
        BrainRingActiveRoom activeRoom = new BrainRingActiveRoom(true, joinCode, players);

        redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()), activeRoom);
        return activeRoom;
    }

    public AnswerResponseDTO submitAnswer(UUID roomId, UUID playerId, double answerTime) {
        BrainRingActiveRoom activeRoom = (BrainRingActiveRoom) redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()));
        if (activeRoom == null || !activeRoom.isReady()) {
            return null;
        }

        activeRoom.setReady(false);
        redisTemplate.opsForValue().set(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()), activeRoom);

        BrainRingPlayer player = activeRoom.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new FizzlyAppException(playerId.toString()));

        LOGGER.info("Answer submitted: player={}, time={}", player.getPlayerName(), answerTime);
        return new AnswerResponseDTO(playerId, player.getPlayerName(), answerTime);
    }

    private BrainRingActiveRoom getActiveRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = (BrainRingActiveRoom) redisTemplate.opsForValue().get(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()));
        if (activeRoom == null) {
            LOGGER.warn("Active room not found: {}", roomId);
            throw new FizzlyAppException(roomId.toString());
        }
        return activeRoom;
    }

    public void activateNextQuestionInRoom(UUID roomId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()));

                BrainRingActiveRoom activeRoom = (BrainRingActiveRoom) operations.opsForValue().get(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()));
                if (activeRoom == null) {
                    operations.unwatch();
                    throw new FizzlyAppException(roomId.toString());
                }

                operations.multi();
                activeRoom.setReady(true);
                operations.opsForValue().set(RedisKeys.buildKey(RedisKeys.BRAINRING_ACTIVE_ROOM_KEY, roomId.toString()), activeRoom);

                return operations.exec();
            }
        });

        LOGGER.info("Next question activated for room: {}", roomId);
    }

    public PlayerExistsResponse playerExistsInRoom(UUID roomId, UUID playerId) {
        PlayerExistsResponse response = new PlayerExistsResponse();
        BrainRingPlayer player = (BrainRingPlayer) redisTemplate.opsForHash().get(RedisKeys.buildKey(RedisKeys.BRAINRING_PLAYERS_KEY, roomId.toString()), playerId.toString());

        if (player != null) {
            response.setExists(true);
            response.setPlayerId(playerId);
            response.setRoomId(roomId);
            response.setPlayerName(player.getPlayerName());
        } else {
            response.setExists(false);
        }
        return response;
    }

    public void finishRoom(UUID roomId) {
        deleteRoomData(roomId);
        LOGGER.info("Game session ended for room: {}", roomId);
    }

    private void deleteRoomData(UUID roomId) {
        Set<String> keys = redisTemplate.keys(
                String.format("*%s*", roomId) // Шаблон для поиска всех ключей комнаты
        );

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}
