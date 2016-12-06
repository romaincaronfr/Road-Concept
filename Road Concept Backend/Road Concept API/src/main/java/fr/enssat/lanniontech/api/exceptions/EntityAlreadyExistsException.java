package fr.enssat.lanniontech.api.exceptions;


public class EntityAlreadyExistsException extends DatabaseOperationException { //NOSONAR

    /**
     * Constructs a new EntityAlreadyExistsException with specified detail message.
     */
    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructs a new EntityAlreadyExistsException with specified detail message
     * and nested Throwable.
     */
    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new EntityAlreadyExistsException with specified nested Throwable
     * and default message.
     */
    public EntityAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
