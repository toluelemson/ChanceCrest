package com.bombaylive.chancecrest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Handles global exceptions for the application, returning standardized error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends Throwable {

    /**
     * Handle exceptions related to invalid bet amounts.
     *
     * @param ex The caught exception.
     * @return A standardized error response.
     */
    @ExceptionHandler(InvalidBetAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBetAmountException(InvalidBetAmountException ex) {
        return buildErrorResponse(ex);
    }

    /**
     * Handle exceptions related to invalid number choices.
     *
     * @param ex The caught exception.
     * @return A standardized error response.
     */
    @ExceptionHandler(InvalidNumberChoiceException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNumberChoiceException(InvalidNumberChoiceException ex) {
        return buildErrorResponse(ex);
    }

    /**
     * Creates an error response entity based on the provided exception and status.
     *
     * @param ex The exception.
     * @return The error response entity.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
