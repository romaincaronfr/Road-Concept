package fr.enssat.lanniontech.api.exceptions;

public class AuthenticationException extends RoadConceptException {

    /**
     * Constructs a new AuthenticationException with specified detail message.
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with specified detail message
     * and nested Throwable.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AuthenticationException with specified nested Throwable
     * and default message.
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
