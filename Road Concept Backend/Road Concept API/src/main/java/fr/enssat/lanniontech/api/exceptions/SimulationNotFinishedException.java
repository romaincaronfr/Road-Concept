package fr.enssat.lanniontech.api.exceptions;

public class SimulationNotFinishedException extends RoadConceptException {

    /**
     * Constructs a new SimulationNotFinishedException with default message.
     */
    public SimulationNotFinishedException() {

    }

    /**
     * Constructs a new SimulationNotFinishedException with specified detail message.
     */
    public SimulationNotFinishedException(String message) {
        super(message);
    }

    /**
     * Constructs a new SimulationNotFinishedException with specified detail message
     * and nested Throwable.
     */
    public SimulationNotFinishedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SimulationNotFinishedException with specified nested Throwable
     * and default message.
     */
    public SimulationNotFinishedException(Throwable cause) {
        super(cause);
    }
}
