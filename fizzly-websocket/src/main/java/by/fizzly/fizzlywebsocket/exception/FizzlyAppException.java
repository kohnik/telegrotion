package by.fizzly.fizzlywebsocket.exception;

public class FizzlyAppException extends RuntimeException {

    public FizzlyAppException() {
    }

    public FizzlyAppException(String message) {
        super(message);
    }

    public FizzlyAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
