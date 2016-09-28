package fr.enssat.lanniontech.exceptions;

public class MapJSONProcessingException extends RuntimeException {

    public MapJSONProcessingException(String fileName, Throwable cause) {
        super("An unexpected error occured while processing '" + fileName + "'.", cause);
    }

    public MapJSONProcessingException(Throwable cause) {
        super(cause);
    }
}
