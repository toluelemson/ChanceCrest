package com.bombaylive.chancecrest.exception;
/**
 * Exception indicating that the number choice for a bet is invalid.
 */
public class InvalidNumberChoiceException extends RuntimeException {
    /**
     * Constructs a new InvalidNumberChoiceException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidNumberChoiceException(String message) {
        super(message);
    }
}

