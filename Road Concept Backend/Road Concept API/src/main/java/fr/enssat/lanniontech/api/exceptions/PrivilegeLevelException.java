package fr.enssat.lanniontech.api.exceptions;

public class PrivilegeLevelException extends RoadConceptException {

    /**
     * Constructs a new PrivilegeLevelException with specified detail message.
     */
    public PrivilegeLevelException(String message) {
        super(message);
    }

    /**
     * Constructs a new PrivilegeLevelException with specified detail message
     * and nested Throwable.
     */
    public PrivilegeLevelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new PrivilegeLevelException with specified nested Throwable
     * and default message.
     */
    public PrivilegeLevelException(Throwable cause) {
        super(cause);
    }
}
