package com.fizzly.backend.service.brainring;

import com.fizzly.backend.exception.InvalidJoinCodeException;
import com.fizzly.backend.exception.TelegrotionException;
import com.fizzly.backend.utils.JoinCodeUtils;
import com.fizzly.backend.websocket.braintring.BrainRingController;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BrainRingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrainRingService.class);

    private static final Map<UUID, String> rooms = new HashMap();
    private static final Map<UUID, List<BrainRingTeam>> teams = new HashMap();
    private static final Map<UUID, BrainRingActiveRoom> activeRooms = new HashMap<>();

    public BrainRingRoomDTO createRoom() {
        String joinCode = JoinCodeUtils.generateJoinCode();
        UUID roomId = UUID.randomUUID();
        rooms.put(roomId, joinCode);

        return new BrainRingRoomDTO(roomId, joinCode);
    }

    public BrainRingJoinRoomDTO joinRoom(String joinCode, String teamName) {
        Map.Entry<UUID, String> room = getRoomByJoinCode(joinCode);
        if (teamExists(teamName)) {
            throw new TelegrotionException("Team " + teamName + " already exists");
        }

        UUID teamId = UUID.randomUUID();
        BrainRingTeam brainRingTeam = new BrainRingTeam(teamId, teamName);

        List<BrainRingTeam> brainRingTeams = teams.getOrDefault(room.getKey(), new ArrayList<>());
        brainRingTeams.add(brainRingTeam);
        teams.put(room.getKey(), brainRingTeams);

        return new BrainRingJoinRoomDTO(room.getKey(), joinCode, teamName, teamId);
    }

    public void deleteTeam(UUID teamId, UUID roomId) {
        if (!teamExists(teamId)) {
            throw new TelegrotionException("Team " + teamId + " does not exist");
        }
        List<BrainRingTeam> brainRingTeams = teams.get(roomId);
        brainRingTeams.removeIf(brainRingTeam -> brainRingTeam.getTeamId().equals(teamId));
    }

    public BrainRingRoomFullDTO getRooFullInfo(UUID roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new TelegrotionException("Room " + roomId + " does not exist");
        }
        String joinCode = rooms.get(roomId);

        return new BrainRingRoomFullDTO(roomId, joinCode, teams.get(roomId));
    }

    private Map.Entry<UUID, String> getRoomByJoinCode(String joinCode) {
        return rooms.entrySet().stream()
                .filter(existingJoinCode -> existingJoinCode.getValue().equals(joinCode))
                .findFirst().orElseThrow(InvalidJoinCodeException::new);
    }

    private boolean teamExists(String teamName) {
        return teams.values().stream().flatMap(Collection::stream).map(BrainRingTeam::getTeamName)
                .anyMatch(teamName::equals);
    }

    public BrainRingActiveRoom activateRoom(UUID roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new TelegrotionException("Room " + roomId + " does not exist");
        }
        String joinCode = rooms.get(roomId);
        List<BrainRingTeam> activeTeams = teams.get(roomId);

        BrainRingActiveRoom activeRoom = new BrainRingActiveRoom(true, joinCode, activeTeams);
        activeRooms.put(roomId, activeRoom);

        return activeRoom;
    }

    private boolean teamExists(UUID teamId) {
        return teams.values().stream().flatMap(Collection::stream).map(BrainRingTeam::getTeamId)
                .anyMatch(teamId::equals);
    }

    public BrainRingController.AnswerResponseDTO submitAnswer(UUID roomId, UUID teamId, double answerTime) {
        BrainRingActiveRoom activeRoom = getActiveRoom(roomId);
        if (!activeRoom.ready) {
            return null;
        }

        activeRoom.setReady(false);
        activeRooms.put(roomId, activeRoom);

        LOGGER.info("Team %s submitted answer with time %d", teamId.toString(), answerTime);

        final BrainRingTeam brainRingTeam = activeRoom.getTeams().stream()
                .filter(team -> team.getTeamId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new TelegrotionException("Team " + teamId + " does not exist"));
        return new BrainRingController.AnswerResponseDTO(teamId, brainRingTeam.getTeamName(), answerTime);
    }

    private BrainRingActiveRoom getActiveRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = activeRooms.get(roomId);
        if (activeRoom == null) {
            throw new TelegrotionException("Room " + roomId + " does not exist");
        }
        return activeRoom;
    }

    public void activateNextQuestionInRoom(UUID roomId) {
        BrainRingActiveRoom activeRoom = getActiveRoom(roomId);

        activeRoom.setReady(true);
        activeRooms.put(roomId, activeRoom);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BrainRingRoomDTO {
        private UUID roomId;
        private String joinCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BrainRingJoinRoomDTO {
        private UUID roomId;
        private String joinCode;
        private String teamName;
        private UUID teamId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BrainRingTeam {
        private UUID teamId;
        private String teamName;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class BrainRingRoomFullDTO {
        private UUID roomId;
        private String joinCode;
        private List<BrainRingTeam> teams;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrainRingActiveRoom {
        private boolean ready;
        private String joinCode;
        private List<BrainRingTeam> teams;
    }

}
