package fr.enssat.lanniontech.api.exceptions;


public class EntityStillInUseException extends DatabaseOperationException {

    public EntityStillInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
