package com.telegrotion.backend.exception;

public class EntityNotFoundException extends TelegrotionException {

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
