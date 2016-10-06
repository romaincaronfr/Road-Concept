package fr.enssat.lanniontech.api.exceptions;

public class AuthenticationException extends RoadConceptException {

    /**
     * Constructs a new AuthenticationException with default message.
     */
    public AuthenticationException() {
        super();
    }

    /**
     * Constructs a new AuthenticationException with specified detail message.
     */
    public AuthenticationException(final String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with specified detail message
     * and nested Throwable.
     */
    public AuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AuthenticationException with specified nested Throwable
     * and default message.
     */
    public AuthenticationException(final Throwable cause) {
        super(cause);
    }
}
