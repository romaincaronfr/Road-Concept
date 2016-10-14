package fr.enssat.lanniontech.api.exceptions;

public class DatabaseOperationException extends RoadConceptException {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
