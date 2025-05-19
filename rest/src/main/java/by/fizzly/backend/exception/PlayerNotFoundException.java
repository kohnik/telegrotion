package by.fizzly.backend.exception;

import java.util.UUID;

public class PlayerNotFoundException extends EntityNotFoundException {

    public PlayerNotFoundException(String playerName) {
        super(String.format("Игрок с никнеймом %s не найден", playerName));
    }

    public PlayerNotFoundException(UUID playerId) {
        super(String.format("Игрок с ID %s не найден", playerId));
    }
}
