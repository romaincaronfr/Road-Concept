package fr.enssat.lanniontech.exceptions;

public class JSONProcessingException extends RuntimeException {

    public JSONProcessingException(String fileName, Throwable cause) {
        super("An unexpected error occured while processing '" + fileName + "'.", cause);
    }

    public JSONProcessingException(Throwable cause) {
        super(cause);
    }
}
