package by.fizzly.fizzlywebsocket.exception;

import java.util.UUID;

public class UserNotFoundException extends QuizException {
    public UserNotFoundException(UUID playerId) {
        super(String.format("User with id %s not found", playerId));
    }

    public UserNotFoundException(String username) {
        super(String.format("User with username %s not found", username));
    }
} 