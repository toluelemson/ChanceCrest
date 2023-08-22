package com.bombaylive.chancecrest.util;
import java.util.Random;


/**
 * Utility class to generate server numbers.
 */

public class ServerNumberGenerator {
    private static final Random random = new Random();
    private static final int MIN_SERVER_NUMBER = 1;
    private static final int MAX_SERVER_NUMBER = 100;

    /**
     * Generates a random number between MIN_SERVER_NUMBER and MAX_SERVER_NUMBER.
     *
     * @return the generated random number.
     */

    public static int generate() {
        return random.nextInt((MAX_SERVER_NUMBER - MIN_SERVER_NUMBER) + 1) + MIN_SERVER_NUMBER;
    }
}
