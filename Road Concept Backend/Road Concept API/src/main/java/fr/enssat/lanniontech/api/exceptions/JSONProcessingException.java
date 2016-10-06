package fr.enssat.lanniontech.api.exceptions;

public class JSONProcessingException extends RoadConceptException {

    /**
     * Constructs a new JSONProcessingException with default message.
     */
    public JSONProcessingException() {
        super();
    }

    /**
     * Constructs a new JSONProcessingException with specified detail message.
     */
    public JSONProcessingException(final String message) {
        super(message);
    }

    /**
     * Constructs a new JSONProcessingException with specified detail message
     * and nested Throwable.
     */
    public JSONProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JSONProcessingException with specified nested Throwable
     * and default message.
     */
    public JSONProcessingException(final Throwable cause) {
        super(cause);
    }
}
