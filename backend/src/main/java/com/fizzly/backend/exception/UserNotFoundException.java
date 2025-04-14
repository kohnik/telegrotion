package com.fizzly.backend.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long userId) {
        super(String.format("User with id %d not found", userId));
    }

    public UserNotFoundException(String username) {
        super(String.format("User with username %s not found", username));
    }
}
