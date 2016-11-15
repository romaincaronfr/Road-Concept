package fr.enssat.lanniontech.api.exceptions;

public class SimulatorUnavailableException extends RoadConceptException {

    /**
     * Constructs a new SimulatorUnavailableException with default message.
     */
    public SimulatorUnavailableException() {

    }

    /**
     * Constructs a new SimulatorUnavailableException with specified detail message.
     */
    public SimulatorUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new SimulatorUnavailableException with specified detail message
     * and nested Throwable.
     */
    public SimulatorUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SimulatorUnavailableException with specified nested Throwable
     * and default message.
     */
    public SimulatorUnavailableException(Throwable cause) {
        super(cause);
    }
}
