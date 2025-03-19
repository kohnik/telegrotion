package com.telegrotion.backend.exception;

public class TelegrotionException extends RuntimeException {

    public TelegrotionException() {
    }

    public TelegrotionException(String message) {
        super(message);
    }

    public TelegrotionException(String message, Throwable cause) {
        super(message, cause);
    }
}
