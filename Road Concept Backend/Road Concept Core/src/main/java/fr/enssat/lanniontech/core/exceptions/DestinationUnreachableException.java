package fr.enssat.lanniontech.core.exceptions;

public class DestinationUnreachableException extends RuntimeException {

    /**
     * Constructs a new DestinationUnreachableException with default message.
     */
    public DestinationUnreachableException() {

    }

    /**
     * Constructs a new DestinationUnreachableException with specified detail message.
     */
    public DestinationUnreachableException(String message) {
        super(message);
    }

    /**
     * Constructs a new DestinationUnreachableException with specified detail message
     * and nested Throwable.
     */
    public DestinationUnreachableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DestinationUnreachableException with specified nested Throwable
     * and default message.
     */
    public DestinationUnreachableException(Throwable cause) {
        super(cause);
    }
}
