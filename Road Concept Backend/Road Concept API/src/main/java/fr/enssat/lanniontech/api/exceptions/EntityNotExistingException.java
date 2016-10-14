package fr.enssat.lanniontech.api.exceptions;


public class EntityNotExistingException extends RuntimeException {

    private Class entityClass;

    public EntityNotExistingException() {

    }

    public EntityNotExistingException(Class entityClass) {
        this.entityClass = entityClass;
    }

    public String getEntityClass() {
        return entityClass.toString();
    }

    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }
}
