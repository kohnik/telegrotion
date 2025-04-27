package com.fizzly.backend.exception;

import java.util.UUID;

public class PLayerNotFoundException extends EntityNotFoundException {

    public PLayerNotFoundException(String playerName) {
        super(String.format("Игрок с никнеймом %s не найден", playerName));
    }

    public PLayerNotFoundException(UUID playerId) {
        super(String.format("Игрок с ID %s не найден", playerId));
    }
}
