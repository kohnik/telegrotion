package com.fizzly.backend.controller.brainring;

import by.fizzly.common.dto.brainring.BrainRingEndSessionRequest;
import by.fizzly.common.dto.brainring.BrainRingJoinRoomDTO;
import by.fizzly.common.dto.brainring.BrainRingRoomDTO;
import by.fizzly.common.dto.brainring.BrainRingRoomFullDTO;
import by.fizzly.common.dto.brainring.DeletePlayerRequestDTO;
import by.fizzly.common.dto.brainring.JoinRoomRequestDTO;
import by.fizzly.common.dto.brainring.PlayerExistsRequest;
import by.fizzly.common.dto.brainring.PlayerExistsResponse;
import by.fizzly.common.dto.brainring.RoomDescriptionDTO;
import by.fizzly.common.dto.websocket.response.BrainRingSessionEndedResponse;
import by.fizzly.common.event.BrainRingEvent;
import com.fizzly.backend.service.brainring.BrainRingService;
import com.fizzly.backend.utils.WebSocketTopics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/create-room")
    @Operation(summary = "Создать комнату")
    public ResponseEntity<BrainRingRoomDTO> createRoom() {
        return ResponseEntity.status(HttpStatus.CREATED).body(brainRingService.createRoom());
    }

    @PostMapping("/join-room")
    @Operation(summary = "Присоединить команду")
    public ResponseEntity<BrainRingJoinRoomDTO> joinRoom(@RequestBody JoinRoomRequestDTO request) {
        BrainRingJoinRoomDTO roomDTO = brainRingService.joinRoom(request.getJoinCode(), request.getPlayerName());

        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, roomDTO.getRoomId());
        BrainRingRoomFullDTO rooFullInfo = brainRingService.getRoomFullInfo(roomDTO.getRoomId());
        messagingTemplate.convertAndSend(topic, new RoomDescriptionDTO(
                BrainRingEvent.USER_ADDED.getId(),
                request.getPlayerName(),
                request.getJoinCode(),
                roomDTO.getPlayerId(),
                rooFullInfo.getPlayers().size()
        ));

        return ResponseEntity.ok(roomDTO);
    }

    @PostMapping("/player-exists")
    @Operation(summary = "Проверка есть ли такой игрок в игры")
    public ResponseEntity<PlayerExistsResponse> playerExistsInSessionRoom(@RequestBody PlayerExistsRequest request) {
        return ResponseEntity.ok(
                brainRingService.playerExistsInRoom(request.getRoomId(), request.getPlayerId())
        );
    }

    @DeleteMapping("/teams")
    @Operation(summary = "Удалить команду")
    public ResponseEntity<Void> deleteTeamById(@RequestBody DeletePlayerRequestDTO request) {
        brainRingService.deletePlayer(request.getPlayerId(), request.getRoomId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/{roomId}")
    @Operation(summary = "Полная инфа по комнате")
    public ResponseEntity<BrainRingRoomFullDTO> getRoomFull(@PathVariable UUID roomId) {
        return ResponseEntity.ok(brainRingService.getRoomFullInfo(roomId));
    }

    @PostMapping("/rooms/finish")
    @Operation(summary = "Завершение игровой сессии")
    public ResponseEntity<Void> finishRoom(@RequestBody BrainRingEndSessionRequest request) {
        brainRingService.finishRoom(request.getRoomId());

        String topic = String.format(WebSocketTopics.JOIN_BRAIN_RING_TOPIC, request.getRoomId());
        messagingTemplate.convertAndSend(topic, new BrainRingSessionEndedResponse(
                BrainRingEvent.SESSION_ENDED.getId(),
                request.getRoomId()
        ));

        return ResponseEntity.ok().build();
    }

}
