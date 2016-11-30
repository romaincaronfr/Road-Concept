package fr.enssat.lanniontech.api.exceptions;

public class ProgressUnavailableException extends RoadConceptException {

    /**
     * Constructs a new ProgressUnavailableException with default message.
     */
    public ProgressUnavailableException() {
        super("The execution progress of the given simulation is not available. Wait for the simulation to terminate.");
    }

    /**
     * Constructs a new ProgressUnavailableException with specified detail message.
     */
    public ProgressUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new ProgressUnavailableException with specified detail message
     * and nested Throwable.
     */
    public ProgressUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ProgressUnavailableException with specified nested Throwable
     * and default message.
     */
    public ProgressUnavailableException(Throwable cause) {
        super(cause);
    }
}
