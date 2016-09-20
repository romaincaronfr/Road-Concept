package fr.enssat.lanniontech.injector.osm.entities.exceptions;

/**
 * Created by maelig on 20/09/2016.
 */
public class OSMFileParsingException extends RuntimeException {

    public OSMFileParsingException(String fileName, Throwable cause) {
        super("An unexpected error occured while parsing file '" + fileName + "'.", cause);
    }

    public OSMFileParsingException(Throwable cause) {
        super(cause);
    }
}
