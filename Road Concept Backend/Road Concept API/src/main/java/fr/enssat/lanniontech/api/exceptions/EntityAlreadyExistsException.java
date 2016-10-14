package fr.enssat.lanniontech.api.exceptions;


public class EntityAlreadyExistsException extends DatabaseOperationException {

    public EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
