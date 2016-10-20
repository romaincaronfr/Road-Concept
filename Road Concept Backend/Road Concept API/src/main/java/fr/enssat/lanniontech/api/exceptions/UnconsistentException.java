package fr.enssat.lanniontech.api.exceptions;

public class UnconsistentException extends RoadConceptException {

    /**
     * Constructs a new AuthenticationException with default message.
     */
    public UnconsistentException() {

    }

    /**
     * Constructs a new AuthenticationException with specified detail message.
     */
    public UnconsistentException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with specified detail message
     * and nested Throwable.
     */
    public UnconsistentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AuthenticationException with specified nested Throwable
     * and default message.
     */
    public UnconsistentException(Throwable cause) {
        super(cause);
    }
}
