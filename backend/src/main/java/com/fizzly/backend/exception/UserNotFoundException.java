package com.fizzly.backend.exception;

import java.util.UUID;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long userId) {
        super(String.format("User with id %d not found", userId));
    }

    public UserNotFoundException(String username) {
        super(String.format("User with username %s not found", username));
    }

    public UserNotFoundException(UUID playerId) {
        super(String.format("User with id %s not found", playerId));
    }
}
