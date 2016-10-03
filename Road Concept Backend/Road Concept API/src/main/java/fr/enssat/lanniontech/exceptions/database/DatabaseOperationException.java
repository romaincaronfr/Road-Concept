package fr.enssat.lanniontech.exceptions.database;

import fr.enssat.lanniontech.exceptions.RoadConceptException;

// TODO: Find a better name
public class DatabaseOperationException extends RoadConceptException {

    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
