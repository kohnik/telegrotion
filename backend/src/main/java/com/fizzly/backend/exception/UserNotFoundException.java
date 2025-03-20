package com.fizzly.backend.exception;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long userId) {
        super(String.format("User with id %d not found", userId));
    }
}
