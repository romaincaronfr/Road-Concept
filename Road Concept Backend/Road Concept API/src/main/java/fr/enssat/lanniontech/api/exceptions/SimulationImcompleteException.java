package fr.enssat.lanniontech.api.exceptions;

public class SimulationImcompleteException extends RoadConceptException {

    /**
     * Constructs a new SimulationImcompleteException with default message.
     */
    public SimulationImcompleteException() {

    }

    /**
     * Constructs a new SimulationImcompleteException with specified detail message.
     */
    public SimulationImcompleteException(String message) {
        super(message);
    }

    /**
     * Constructs a new SimulationImcompleteException with specified detail message
     * and nested Throwable.
     */
    public SimulationImcompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SimulationImcompleteException with specified nested Throwable
     * and default message.
     */
    public SimulationImcompleteException(Throwable cause) {
        super(cause);
    }
}
