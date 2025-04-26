package com.fizzly.backend.exception;

import java.util.UUID;

public class RoomNotFoundException extends EntityNotFoundException {

    public RoomNotFoundException(UUID id) {
        super(String.format("Комната с ID %s не найдена", id));
    }

    public RoomNotFoundException(String joinCode) {
        super(String.format("Комната с кодом %s не найдена", joinCode));
    }
}
