package fr.enssat.lanniontech.api.exceptions;

public abstract class RoadConceptException extends RuntimeException {

    private String message;
    private Throwable cause;

    public RoadConceptException() {

    }

    public RoadConceptException(String message) {
        this.message = message;
    }

    public RoadConceptException(Throwable cause) {
        this.cause = cause;
    }

    public RoadConceptException(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }
}
