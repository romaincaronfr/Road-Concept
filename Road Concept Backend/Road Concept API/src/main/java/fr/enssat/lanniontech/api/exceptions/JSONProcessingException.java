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
     *
     * @param message
     */
    public JSONProcessingException(final String message) {
        super(message);
    }

    /**
     * Constructs a new JSONProcessingException with specified detail message
     * and nested Throwable.
     *
     * @param message
     * @param cause
     */
    public JSONProcessingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new JSONProcessingException with specified nested Throwable
     * and default message.
     *
     * @param cause
     */
    public JSONProcessingException(final Throwable cause) {
        super(cause);
    }
}
