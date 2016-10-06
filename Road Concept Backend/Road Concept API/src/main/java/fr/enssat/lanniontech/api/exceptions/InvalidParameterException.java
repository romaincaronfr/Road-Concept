package fr.enssat.lanniontech.api.exceptions;

public class InvalidParameterException extends RoadConceptException {

    /**
     * Constructs a new InvalidParameterException with default message.
     */
    public InvalidParameterException() {
        super();
    }

    /**
     * Constructs a new InvalidParameterException with specified detail message.
     */
    public InvalidParameterException(final String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidParameterException with specified detail message
     * and nested Throwable.
     */
    public InvalidParameterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidParameterException with specified nested Throwable
     * and default message.
     */
    public InvalidParameterException(final Throwable cause) {
        super(cause);
    }
}
