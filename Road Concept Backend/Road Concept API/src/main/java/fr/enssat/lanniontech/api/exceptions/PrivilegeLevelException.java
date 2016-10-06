package fr.enssat.lanniontech.api.exceptions;

public class PrivilegeLevelException extends RoadConceptException {

    /**
     * Constructs a new PrivilegeLevelException with default message.
     */
    public PrivilegeLevelException() {
        super();
    }

    /**
     * Constructs a new PrivilegeLevelException with specified detail message.
     */
    public PrivilegeLevelException(final String message) {
        super(message);
    }

    /**
     * Constructs a new PrivilegeLevelException with specified detail message
     * and nested Throwable.
     */
    public PrivilegeLevelException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new PrivilegeLevelException with specified nested Throwable
     * and default message.
     */
    public PrivilegeLevelException(final Throwable cause) {
        super(cause);
    }
}
