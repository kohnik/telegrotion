package com.fizzly.backend.controller.brainring;

import com.fizzly.backend.service.brainring.BrainRingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/brain-ring")
@RequiredArgsConstructor
@Tag(name = "Brain Ring", description = "API управления сессиями Brain Ring")
public class BrainRingSessionController {

    private final BrainRingService brainRingService;

    @PostMapping("/create-room")
    @Operation(summary = "Создать комнату")
    public ResponseEntity<BrainRingService.BrainRingRoomDTO> createRoom() {
        return ResponseEntity.status(HttpStatus.CREATED).body(brainRingService.createRoom());
    }

    @PostMapping("/join-room")
    @Operation(summary = "Присоединить команду")
    public ResponseEntity<BrainRingService.BrainRingJoinRoomDTO> joinRoom(@RequestBody JoinRoomRequestDTO request) {
        return ResponseEntity.ok(brainRingService.joinRoom(request.joinCode, request.teamName));
    }

    @DeleteMapping("/teams")
    @Operation(summary = "Удалить команду")
    public ResponseEntity<Void> deleteTeamById(@RequestBody DeleteTeamRequestDTO request) {
        brainRingService.deleteTeam(request.teamName, request.getRoomId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Полная инфа по комнате")
    public ResponseEntity<BrainRingService.BrainRingRoomFullDTO> getRoomFull(@PathVariable UUID roomId) {
        return ResponseEntity.ok(brainRingService.getRooFullInfo(roomId));
    }

    @Getter
    @Setter
    public static class JoinRoomRequestDTO {
        private String teamName;
        private String joinCode;
    }

    @Getter
    @Setter
    public static class DeleteTeamRequestDTO {
        private String teamName;
        private UUID roomId;
    }
}
