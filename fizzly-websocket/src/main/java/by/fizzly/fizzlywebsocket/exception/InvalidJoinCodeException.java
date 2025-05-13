package by.fizzly.fizzlywebsocket.exception;

public class InvalidJoinCodeException extends QuizException {
    public InvalidJoinCodeException() {
        super("Неправильный код комнаты");
    }
} 