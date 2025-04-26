package com.fizzly.backend.service.brainring;

import com.fizzly.backend.dto.brainring.AnswerResponseDTO;
import com.fizzly.backend.dto.brainring.BrainRingActiveRoom;
import com.fizzly.backend.dto.brainring.BrainRingJoinRoomDTO;
import com.fizzly.backend.dto.brainring.BrainRingPlayer;
import com.fizzly.backend.dto.brainring.BrainRingRoomDTO;
import com.fizzly.backend.dto.brainring.BrainRingRoomFullDTO;
import com.fizzly.backend.dto.brainring.PlayerExistsResponse;
import com.fizzly.backend.exception.PLayerNotFoundException;
import com.fizzly.backend.exception.RoomNotFoundException;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.utils.JoinCodeUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrainRingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainRingService.class);

    private final RedisTemplate<UUID, String> roomRedisTemplate;
    private final RedisTemplate<String, UUID> roomRedisTemplateInvert;
    private final RedisTemplate<String, List<BrainRingPlayer>> teamRedisTemplate;
    private final RedisTemplate<String, BrainRingActiveRoom> activeRoomRedisTemplate;

    public BrainRingRoomDTO createRoom() {
        String joinCode = JoinCodeUtils.generateJoinCode();
        UUID roomId = UUID.randomUUID();
        roomRedisTemplate.opsForValue().set(roomId, joinCode);
        roomRedisTemplateInvert.opsForValue().set(joinCode, roomId);

        return new BrainRingRoomDTO(roomId, joinCode);
    }

    public BrainRingJoinRoomDTO joinRoom(String joinCode, String playerName) {
        UUID roomId = getRoomByJoinCode(joinCode);
        if (roomId == null) {
            throw new TelegrotionException("Не найдена комната с кодом комнаты: " + joinCode);
        }
        if (playerExists(roomId, playerName)) {
            String exMessage = String.format("Игрок с никнеймом %s уже существует", playerName);
            LOGGER.error(exMessage);
            throw new TelegrotionException(exMessage);
        }

        UUID playerId = UUID.randomUUID();
        BrainRingPlayer brainRingPlayer = new BrainRingPlayer(playerId, playerName);

        List<BrainRingPlayer> brainRingPlayers;
        if (teamRedisTemplate.hasKey("team:" + roomId)) {
            brainRingPlayers = teamRedisTemplate.opsForValue().get("team:" + roomId);
        } else {
            brainRingPlayers = new ArrayList<>();
        }

        brainRingPlayers.add(brainRingPlayer);
        teamRedisTemplate.opsForValue().set("team:" + roomId, brainRingPlayers);
        LOGGER.info("Player with id {} created", playerId);

        return new BrainRingJoinRoomDTO(roomId, joinCode, playerName, playerId);
    }

    public void deleteTeam(UUID playerId, UUID roomId) {
        if (!playerExists(roomId, playerId)) {
            throw new PLayerNotFoundException(playerId);
        }
        List<BrainRingPlayer> brainRingPlayers = teamRedisTemplate.opsForValue().get("team:" + roomId);
        brainRingPlayers.removeIf(brainRingTeam -> brainRingTeam.getPlayerId().equals(playerId));
    }

    public BrainRingRoomFullDTO getRooFullInfo(UUID roomId) {
        if (!roomRedisTemplate.hasKey(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        String joinCode = roomRedisTemplate.opsForValue().get(roomId);

        return new BrainRingRoomFullDTO(roomId, joinCode, teamRedisTemplate.opsForValue().get("team:" + roomId));
    }

    private UUID getRoomByJoinCode(String joinCode) {
        return roomRedisTemplateInvert.opsForValue().get(joinCode);
    }

    private boolean playerExists(UUID roomId, String playerName) {
        List<BrainRingPlayer> brainRingPlayers = teamRedisTemplate.opsForValue().get("team:" + roomId);
        if (brainRingPlayers == null) {
            return false;
        }
        return brainRingPlayers.stream().map(BrainRingPlayer::getPlayerName)
                .anyMatch(playerName::equals);
    }

    public BrainRingActiveRoom activateRoom(UUID roomId) {
        if (!roomRedisTemplate.hasKey(roomId)) {
            throw new RoomNotFoundException(roomId);
        }
        String joinCode = roomRedisTemplate.opsForValue().get(roomId);
        List<BrainRingPlayer> activeTeams = teamRedisTemplate.opsForValue().get("team:" + roomId);

        BrainRingActiveRoom activeRoom = new BrainRingActiveRoom(true, joinCode, activeTeams);
        activeRoomRedisTemplate.opsForValue().set("activeRoom:" + roomId, activeRoom);

        return activeRoom;
    }

    private boolean playerExists(UUID roomId, UUID playerId) {
        List<BrainRingPlayer> brainRingPlayers = teamRedisTemplate.opsForValue().get("team:" + roomId);
        if (brainRingPlayers == null) {
            return false;
        }
        return brainRingPlayers.stream().map(BrainRingPlayer::getPlayerId)
                .anyMatch(playerId::equals);
    }

    public AnswerResponseDTO submitAnswer(UUID roomId, UUID playerId, double answerTime) {
        BrainRingActiveRoom activeRoom = getActiveRoom(roomId);
        if (!activeRoom.isReady()) {
            return null;
        }

        activeRoom.setReady(false);
        activeRoomRedisTemplate.opsForValue().set("activeRoom:" + roomId, activeRoom);

        LOGGER.info("Team %s submitted answer with time %d", playerId.toString(), answerTime);

        final BrainRingPlayer brainRingPlayer = activeRoom.getPlayers().stream()
                .filter(player -> player.getPlayerId().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new PLayerNotFoundException(playerId));
        return new AnswerResponseDTO(playerId, brainRingPlayer.getPlayerName(), answerTime);
    }

    private BrainRingActiveRoom getActiveRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = activeRoomRedisTemplate.opsForValue().get("activeRoom:" + roomId);
        if (activeRoom == null) {
            throw new RoomNotFoundException(roomId);
        }
        return activeRoom;
    }

    public void activateNextQuestionInRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = getActiveRoom(roomId);

        activeRoom.setReady(true);
        activeRoomRedisTemplate.opsForValue().set("activeRoom:" + roomId, activeRoom);
    }

    public PlayerExistsResponse playerExistsInRoom(UUID roomId, UUID playerId) {
        List<BrainRingPlayer> teams = teamRedisTemplate.opsForValue().get("team:" + roomId);
        PlayerExistsResponse response = new PlayerExistsResponse();
        if (teams == null) {
            response.setExists(false);
            return response;
        }
        Optional<BrainRingPlayer> brainRingTeam = teams.stream()
                .filter(team -> team.getPlayerId().equals(playerId))
                .findFirst();
        if (brainRingTeam.isEmpty()) {
            response.setExists(false);
            return response;
        }
        response.setExists(true);
        response.setPlayerId(playerId);
        response.setRoomId(roomId);
        response.setPlayerName(brainRingTeam.get().getPlayerName());

        return response;
    }

}
