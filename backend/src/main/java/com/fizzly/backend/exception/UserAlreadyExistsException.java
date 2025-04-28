package com.fizzly.backend.exception;

import java.util.UUID;

public class UserAlreadyExistsException extends TelegrotionException {

    public UserAlreadyExistsException(String username, UUID roomId) {
        super(String.format("Пользователь %s уже существует в комнате %s", username, roomId));
    }
}
