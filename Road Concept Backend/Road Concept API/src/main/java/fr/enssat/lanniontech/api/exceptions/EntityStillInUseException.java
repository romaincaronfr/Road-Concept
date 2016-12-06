package fr.enssat.lanniontech.api.exceptions;


public class EntityStillInUseException extends DatabaseOperationException { //NOSONAR

    /**
     * Constructs a new EntityStillInUseException with specified detail message.
     */
    public EntityStillInUseException(String message) {
        super(message);
    }

    /**
     * Constructs a new EntityStillInUseException with specified detail message
     * and nested Throwable.
     */
    public EntityStillInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new EntityStillInUseException with specified nested Throwable
     * and default message.
     */
    public EntityStillInUseException(Throwable cause) {
        super(cause);
    }
}
