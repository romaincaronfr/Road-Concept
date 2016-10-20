package fr.enssat.lanniontech.api.exceptions;

public class InvalidParameterException extends RoadConceptException {

    /**
     * Constructs a new InvalidParameterException with default message.
     */
    public InvalidParameterException() {

    }

    /**
     * Constructs a new InvalidParameterException with specified detail message.
     */
    public InvalidParameterException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidParameterException with specified detail message
     * and nested Throwable.
     */
    public InvalidParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new InvalidParameterException with specified nested Throwable
     * and default message.
     */
    public InvalidParameterException(Throwable cause) {
        super(cause);
    }
}
