package com.fizzly.backend.exception;

public class InvalidJoinCodeException extends TelegrotionException {

    public InvalidJoinCodeException() {
        super("Неправильный код комнаты");
    }
}
