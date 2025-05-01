package com.fizzly.backend.service.brainring;

import by.fizzly.common.dto.brainring.AnswerResponseDTO;
import by.fizzly.common.dto.brainring.BrainRingActiveRoom;
import by.fizzly.common.dto.brainring.BrainRingJoinRoomDTO;
import by.fizzly.common.dto.brainring.BrainRingPlayer;
import by.fizzly.common.dto.brainring.BrainRingRoomDTO;
import by.fizzly.common.dto.brainring.BrainRingRoomFullDTO;
import by.fizzly.common.dto.brainring.PlayerExistsResponse;
import com.fizzly.backend.exception.PLayerNotFoundException;
import com.fizzly.backend.exception.RoomNotFoundException;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.utils.JoinCodeUtils;
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
    private static final String ROOM_PREFIX = "room:";
    private static final String JOIN_CODE_PREFIX = "joinCode:";
    private static final String PLAYERS_PREFIX = "room:players:";
    private static final String ACTIVE_ROOM_PREFIX = "activeRoom:";

    public BrainRingRoomDTO createRoom() {
        String joinCode = JoinCodeUtils.generateJoinCode();
        UUID roomId = UUID.randomUUID();

        redisTemplate.opsForValue().set(ROOM_PREFIX + roomId, joinCode);
        redisTemplate.opsForValue().set(JOIN_CODE_PREFIX + joinCode, roomId.toString());

        LOGGER.info("Created room: id={}, joinCode={}", roomId, joinCode);
        return new BrainRingRoomDTO(roomId, joinCode);
    }

    public BrainRingJoinRoomDTO joinRoom(String joinCode, String playerName) {
        UUID roomId = getRoomByJoinCode(joinCode);
        if (roomId == null) {
            throw new RoomNotFoundException(joinCode);
        }
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(PLAYERS_PREFIX + "names:" + roomId, playerName))) {
            String exMessage = String.format("Игрок с никнеймом %s уже существует", playerName);
            LOGGER.error(exMessage);
            throw new TelegrotionException(exMessage);
        }

        BrainRingPlayer player = new BrainRingPlayer(UUID.randomUUID(), playerName);

        redisTemplate.opsForHash().put(
                PLAYERS_PREFIX + roomId,
                player.getPlayerId().toString(),
                player
        );

        redisTemplate.opsForSet().add(PLAYERS_PREFIX + "names:" + roomId, playerName);

        LOGGER.info("Player joined: roomId={}, player={}", roomId, playerName);
        return new BrainRingJoinRoomDTO(roomId, joinCode, playerName, player.getPlayerId());
    }

    public void deletePlayer(UUID playerId, UUID roomId) {
        BrainRingPlayer player = (BrainRingPlayer) redisTemplate.opsForHash().get(PLAYERS_PREFIX + roomId, playerId.toString());
        if (player == null) {
            throw new PLayerNotFoundException(playerId);
        }

        redisTemplate.opsForHash().delete(PLAYERS_PREFIX + roomId, playerId.toString());
        redisTemplate.opsForSet().remove(PLAYERS_PREFIX + "names:" + roomId, player.getPlayerName());
    }

    public BrainRingRoomFullDTO getRoomFullInfo(UUID roomId) {
        String joinCode = (String) redisTemplate.opsForValue().get(ROOM_PREFIX + roomId);
        if (joinCode == null) {
            throw new RoomNotFoundException(roomId);
        }

        List<BrainRingPlayer> players = redisTemplate.opsForHash()
                .values(PLAYERS_PREFIX + roomId)
                .stream()
                .map(BrainRingPlayer.class::cast)
                .toList();

        return new BrainRingRoomFullDTO(roomId, joinCode, players);
    }

    private UUID getRoomByJoinCode(String joinCode) {
        String roomIdStr = (String) redisTemplate.opsForValue().get(JOIN_CODE_PREFIX + joinCode);
        return roomIdStr != null ? UUID.fromString(roomIdStr) : null;
    }

    public BrainRingActiveRoom activateRoom(UUID roomId) {
        String joinCode = (String) redisTemplate.opsForValue().get(ROOM_PREFIX + roomId);
        if (joinCode == null) {
            throw new RoomNotFoundException(roomId);
        }

        List<BrainRingPlayer> players = getRoomFullInfo(roomId).getPlayers();
        BrainRingActiveRoom activeRoom = new BrainRingActiveRoom(true, joinCode, players);

        redisTemplate.opsForValue().set(ACTIVE_ROOM_PREFIX + roomId, activeRoom);
        return activeRoom;
    }

    public AnswerResponseDTO submitAnswer(UUID roomId, UUID playerId, double answerTime) {
        BrainRingActiveRoom activeRoom = (BrainRingActiveRoom) redisTemplate.opsForValue().get(ACTIVE_ROOM_PREFIX + roomId);
        if (activeRoom == null || !activeRoom.isReady()) {
            return null;
        }

        activeRoom.setReady(false);
        redisTemplate.opsForValue().set(ACTIVE_ROOM_PREFIX + roomId, activeRoom);

        BrainRingPlayer player = activeRoom.getPlayers().stream()
                .filter(p -> p.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PLayerNotFoundException(playerId));

        LOGGER.info("Answer submitted: player={}, time={}", player.getPlayerName(), answerTime);
        return new AnswerResponseDTO(playerId, player.getPlayerName(), answerTime);
    }

    private BrainRingActiveRoom getActiveRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = (BrainRingActiveRoom) redisTemplate.opsForValue().get(ACTIVE_ROOM_PREFIX + roomId);
        if (activeRoom == null) {
            LOGGER.warn("Active room not found: {}", roomId);
            throw new RoomNotFoundException(roomId);
        }
        return activeRoom;
    }

    public void activateNextQuestionInRoom(UUID roomId) {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(ACTIVE_ROOM_PREFIX + roomId);

                BrainRingActiveRoom activeRoom = (BrainRingActiveRoom) operations.opsForValue().get(ACTIVE_ROOM_PREFIX + roomId);
                if (activeRoom == null) {
                    operations.unwatch();
                    throw new RoomNotFoundException(roomId);
                }

                operations.multi();
                activeRoom.setReady(true);
                operations.opsForValue().set(ACTIVE_ROOM_PREFIX + roomId, activeRoom);

                return operations.exec();
            }
        });

        LOGGER.info("Next question activated for room: {}", roomId);
    }

    public PlayerExistsResponse playerExistsInRoom(UUID roomId, UUID playerId) {
        PlayerExistsResponse response = new PlayerExistsResponse();
        BrainRingPlayer player = (BrainRingPlayer) redisTemplate.opsForHash().get(PLAYERS_PREFIX + roomId, playerId.toString());

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
