package by.fizzly.fizzlywebsocket.utils;

import java.util.Random;

public class JoinCodeUtils {

    private JoinCodeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String generateJoinCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
