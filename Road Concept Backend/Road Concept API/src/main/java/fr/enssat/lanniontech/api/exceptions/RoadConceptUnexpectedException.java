package fr.enssat.lanniontech.api.exceptions;

public class RoadConceptUnexpectedException extends RoadConceptException {

    /**
     * Constructs a new RoadConceptUnexpectedException with specified detail message.
     */
    public RoadConceptUnexpectedException(String message) {
        super(message);
    }

    /**
     * Constructs a new RoadConceptUnexpectedException with specified detail message
     * and nested Throwable.
     */
    public RoadConceptUnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new RoadConceptUnexpectedException with specified nested Throwable
     * and default message.
     */
    public RoadConceptUnexpectedException(Throwable cause) {
        super(cause);
    }
}
