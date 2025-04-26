package com.fizzly.backend.service.brainring;

import com.fizzly.backend.dto.brainring.AnswerResponseDTO;
import com.fizzly.backend.dto.brainring.BrainRingActiveRoom;
import com.fizzly.backend.dto.brainring.BrainRingJoinRoomDTO;
import com.fizzly.backend.dto.brainring.BrainRingRoomDTO;
import com.fizzly.backend.dto.brainring.BrainRingRoomFullDTO;
import com.fizzly.backend.dto.brainring.BrainRingTeam;
import com.fizzly.backend.dto.brainring.PlayerExistsResponse;
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
    private final RedisTemplate<String, List<BrainRingTeam>> teamRedisTemplate;
    private final RedisTemplate<String, BrainRingActiveRoom> activeRoomRedisTemplate;

    public BrainRingRoomDTO createRoom() {
        String joinCode = JoinCodeUtils.generateJoinCode();
        UUID roomId = UUID.randomUUID();
        roomRedisTemplate.opsForValue().set(roomId, joinCode);
        roomRedisTemplateInvert.opsForValue().set(joinCode, roomId);

        return new BrainRingRoomDTO(roomId, joinCode);
    }

    public BrainRingJoinRoomDTO joinRoom(String joinCode, String teamName) {
        UUID roomId = getRoomByJoinCode(joinCode);
        if (roomId == null) {
            throw new TelegrotionException("Не найдена комната с кодом комнаты: " + joinCode);
        }
        if (teamExists(roomId, teamName)) {
            String exMessage = "Team " + teamName + " already exists";
            LOGGER.error(exMessage);
            throw new TelegrotionException(exMessage);
        }

        UUID teamId = UUID.randomUUID();
        BrainRingTeam brainRingTeam = new BrainRingTeam(teamId, teamName);

        List<BrainRingTeam> brainRingTeams;
        if (teamRedisTemplate.hasKey("team:" + roomId)) {
            brainRingTeams = teamRedisTemplate.opsForValue().get("team:" + roomId);
        } else {
            brainRingTeams = new ArrayList<>();
        }

        brainRingTeams.add(brainRingTeam);
        teamRedisTemplate.opsForValue().set("team:" + roomId, brainRingTeams);
        LOGGER.info("Team with id %s created", teamId);

        return new BrainRingJoinRoomDTO(roomId, joinCode, teamName, teamId);
    }

    public void deleteTeam(UUID teamId, UUID roomId) {
        if (!teamExists(roomId, teamId)) {
            throw new TelegrotionException("Team " + teamId + " does not exist");
        }
        List<BrainRingTeam> brainRingTeams = teamRedisTemplate.opsForValue().get("team:" + roomId);
        brainRingTeams.removeIf(brainRingTeam -> brainRingTeam.getTeamId().equals(teamId));
    }

    public BrainRingRoomFullDTO getRooFullInfo(UUID roomId) {
        if (!roomRedisTemplate.hasKey(roomId)) {
            throw new TelegrotionException("Room " + roomId + " does not exist");
        }
        String joinCode = roomRedisTemplate.opsForValue().get(roomId);

        return new BrainRingRoomFullDTO(roomId, joinCode, teamRedisTemplate.opsForValue().get("team:" + roomId));
    }

    private UUID getRoomByJoinCode(String joinCode) {
        return roomRedisTemplateInvert.opsForValue().get(joinCode);
    }

    private boolean teamExists(UUID roomId, String teamName) {
        List<BrainRingTeam> brainRingTeams = teamRedisTemplate.opsForValue().get("team:" + roomId);
        if (brainRingTeams == null) {
            return false;
        }
        return brainRingTeams.stream().map(BrainRingTeam::getTeamName)
                .anyMatch(teamName::equals);
    }

    public BrainRingActiveRoom activateRoom(UUID roomId) {
        if (!roomRedisTemplate.hasKey(roomId)) {
            throw new TelegrotionException("Room " + roomId + " does not exist");
        }
        String joinCode = roomRedisTemplate.opsForValue().get(roomId);
        List<BrainRingTeam> activeTeams = teamRedisTemplate.opsForValue().get("team:" + roomId);

        BrainRingActiveRoom activeRoom = new BrainRingActiveRoom(true, joinCode, activeTeams);
        activeRoomRedisTemplate.opsForValue().set("activeRoom:" + roomId, activeRoom);

        return activeRoom;
    }

    private boolean teamExists(UUID roomId, UUID teamId) {
        List<BrainRingTeam> brainRingTeams = teamRedisTemplate.opsForValue().get("team:" + roomId);
        if (brainRingTeams == null) {
            return false;
        }
        return brainRingTeams.stream().map(BrainRingTeam::getTeamId)
                .anyMatch(teamId::equals);
    }

    public AnswerResponseDTO submitAnswer(UUID roomId, UUID teamId, double answerTime) {
        BrainRingActiveRoom activeRoom = getActiveRoom(roomId);
        if (!activeRoom.isReady()) {
            return null;
        }

        activeRoom.setReady(false);
        activeRoomRedisTemplate.opsForValue().set("activeRoom:" + roomId, activeRoom);

        LOGGER.info("Team %s submitted answer with time %d", teamId.toString(), answerTime);

        final BrainRingTeam brainRingTeam = activeRoom.getTeams().stream()
                .filter(team -> team.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TelegrotionException("Team " + teamId + " does not exist"));
        return new AnswerResponseDTO(teamId, brainRingTeam.getTeamName(), answerTime);
    }

    private BrainRingActiveRoom getActiveRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = activeRoomRedisTemplate.opsForValue().get("activeRoom:" + roomId);
        if (activeRoom == null) {
            throw new TelegrotionException("Room " + roomId + " does not exist");
        }
        return activeRoom;
    }

    public void activateNextQuestionInRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = getActiveRoom(roomId);

        activeRoom.setReady(true);
        activeRoomRedisTemplate.opsForValue().set("activeRoom:" + roomId, activeRoom);
    }

    public PlayerExistsResponse playerExistsInRoom(UUID roomId, UUID playerId) {
        List<BrainRingTeam> teams = teamRedisTemplate.opsForValue().get("team:" + roomId);
        PlayerExistsResponse response = new PlayerExistsResponse();
        if (teams == null) {
            response.setExists(false);
            return response;
        }
        Optional<BrainRingTeam> brainRingTeam = teams.stream()
                .filter(team -> team.getTeamId().equals(playerId))
                .findFirst();
        if (brainRingTeam.isEmpty()) {
            response.setExists(false);
            return response;
        }
        response.setExists(true);
        response.setPlayerId(playerId);
        response.setRoomId(roomId);
        response.setTeamName(brainRingTeam.get().getTeamName());

        return response;
    }

}
