package fr.enssat.lanniontech.api.exceptions;


public class EntityNotExistingException extends RuntimeException {

    private final Class entityClass;

    public EntityNotExistingException(Class entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityClass() {
        return entityClass.toString();
    }

}
