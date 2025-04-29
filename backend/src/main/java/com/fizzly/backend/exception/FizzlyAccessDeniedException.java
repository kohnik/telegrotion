package com.fizzly.backend.exception;

public class FizzlyAccessDeniedException extends TelegrotionException {

    public FizzlyAccessDeniedException(String resource) {
        super("Нет доступа к ресурсу: " + resource);
    }
}
