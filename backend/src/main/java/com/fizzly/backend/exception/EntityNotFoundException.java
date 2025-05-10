package com.fizzly.backend.exception;

public class EntityNotFoundException extends FizzlyGlobalException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
