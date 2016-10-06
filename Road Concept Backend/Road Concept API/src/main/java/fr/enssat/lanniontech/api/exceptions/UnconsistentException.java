package fr.enssat.lanniontech.api.exceptions;

public class UnconsistentException extends RoadConceptException {

    /**
     * Constructs a new AuthenticationException with default message.
     */
    public UnconsistentException() {
        super();
    }

    /**
     * Constructs a new AuthenticationException with specified detail message.
     */
    public UnconsistentException(final String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with specified detail message
     * and nested Throwable.
     */
    public UnconsistentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new AuthenticationException with specified nested Throwable
     * and default message.
     */
    public UnconsistentException(final Throwable cause) {
        super(cause);
    }
}
