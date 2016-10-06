package fr.enssat.lanniontech.api.exceptions;

/**
 * Thrown to indicate that a block of code has not been implemented.
 */
public class NotImplementedException extends RoadConceptException {

    /**
     * Constructs a new NotImplementedException with default message.
     */
    public NotImplementedException() {
        super();
    }

    /**
     * Constructs a new NotImplementedException with specified detail message.
     */
    public NotImplementedException(final String message) {
        super(message);
    }

    /**
     * Constructs a new NotImplementedException with specified detail message
     * and nested Throwable.
     */
    public NotImplementedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NotImplementedException with specified nested Throwable
     * and default message.
     */
    public NotImplementedException(final Throwable cause) {
        super(cause);
    }
}
