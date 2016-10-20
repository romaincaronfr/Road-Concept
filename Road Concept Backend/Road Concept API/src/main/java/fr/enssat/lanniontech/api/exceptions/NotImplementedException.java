package fr.enssat.lanniontech.api.exceptions;

/**
 * Thrown to indicate that a block of code has not been implemented.
 */
public class NotImplementedException extends RoadConceptException {

    /**
     * Constructs a new NotImplementedException with default message.
     */
    public NotImplementedException() {

    }

    /**
     * Constructs a new NotImplementedException with specified detail message.
     */
    public NotImplementedException(String message) {
        super(message);
    }

    /**
     * Constructs a new NotImplementedException with specified detail message
     * and nested Throwable.
     */
    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NotImplementedException with specified nested Throwable
     * and default message.
     */
    public NotImplementedException(Throwable cause) {
        super(cause);
    }
}
