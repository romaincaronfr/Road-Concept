package fr.enssat.lanniontech.core.exceptions;

public class OutOfBoundException extends RuntimeException{
    /**
     * Constructs a new OutOfBoundException with default message.
     */
    public OutOfBoundException() {
    }

    /**
     * Constructs a new OutOfBoundException with specified detail message.
     */
    public OutOfBoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new OutOfBoundException with specified detail message
     * and nested Throwable.
     */
    public OutOfBoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new OutOfBoundException with specified nested Throwable
     * and default message.
     */
    public OutOfBoundException(Throwable cause) {
        super(cause);
    }
}
