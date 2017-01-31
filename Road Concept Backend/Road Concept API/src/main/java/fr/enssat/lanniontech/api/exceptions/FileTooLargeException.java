package fr.enssat.lanniontech.api.exceptions;


public class FileTooLargeException extends DatabaseOperationException { //NOSONAR

    /**
     * Constructs a new EntityStillInUseException with specified detail message.
     */
    public FileTooLargeException(String message) {
        super(message);
    }

    /**
     * Constructs a new EntityStillInUseException with specified detail message
     * and nested Throwable.
     */
    public FileTooLargeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new EntityStillInUseException with specified nested Throwable
     * and default message.
     */
    public FileTooLargeException(Throwable cause) {
        super(cause);
    }
}
