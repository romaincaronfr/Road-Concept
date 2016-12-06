package fr.enssat.lanniontech.api.exceptions;

public abstract class RoadConceptException extends RuntimeException {

    public RoadConceptException(String message) {
        super(message);
    }

    public RoadConceptException(Throwable cause) {
        super(cause);
    }

    public RoadConceptException(String message, Throwable cause) {
        super(message, cause);
    }
}
