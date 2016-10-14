package fr.enssat.lanniontech.api.exceptions;

public class SQLUnexpectedException extends DatabaseOperationException {

    public SQLUnexpectedException(Exception e) {
        super("An unexpected error occured", e);
    }
}
