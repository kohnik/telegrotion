package by.fizzly.backend.exception;

public class FizzlyGlobalException extends RuntimeException {

    public FizzlyGlobalException() {
    }

    public FizzlyGlobalException(String message) {
        super(message);
    }

    public FizzlyGlobalException(String message, Throwable cause) {
        super(message, cause);
    }
}
