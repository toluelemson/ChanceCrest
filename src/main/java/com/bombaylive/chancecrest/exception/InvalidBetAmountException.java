package com.bombaylive.chancecrest.exception;

/**
 * Exception indicating that the bet amount is invalid.
 */
public class InvalidBetAmountException extends RuntimeException {

    /**
     * Constructs a new InvalidBetAmountException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidBetAmountException(String message) {
        super(message);
    }
}
