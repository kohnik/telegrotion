package by.fizzly.backend.exception;

public class InvalidJoinCodeException extends FizzlyGlobalException {

    public InvalidJoinCodeException() {
        super("Неправильный код комнаты");
    }
}
