package com.fizzly.backend.exception;

public class FizzlyAccessDeniedException extends FizzlyGlobalException {

    public FizzlyAccessDeniedException(String resource) {
        super("Нет доступа к ресурсу: " + resource);
    }
}
