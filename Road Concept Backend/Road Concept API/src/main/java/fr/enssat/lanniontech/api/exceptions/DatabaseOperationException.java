package fr.enssat.lanniontech.api.exceptions;

public class DatabaseOperationException extends RoadConceptException {

    /**
     * Constructs a new DatabaseOperationException with specified detail message.
     */
    public DatabaseOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new DatabaseOperationException with specified detail message
     * and nested Throwable.
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DatabaseOperationException with specified nested Throwable
     * and default message.
     */
    public DatabaseOperationException(Throwable cause) {
        super(cause);
    }}
