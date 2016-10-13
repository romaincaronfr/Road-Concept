package fr.enssat.lanniontech.api.exceptions.database;

import fr.enssat.lanniontech.api.exceptions.RoadConceptException;

public class DatabaseOperationException extends RoadConceptException {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
