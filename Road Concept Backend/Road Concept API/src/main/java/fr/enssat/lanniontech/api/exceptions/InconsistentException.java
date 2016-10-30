package fr.enssat.lanniontech.api.exceptions;

import fr.enssat.lanniontech.api.entities.Entity;

public class InconsistentException extends RoadConceptException {

    /**
     * Constructs a new InconsistentException.
     */
    public InconsistentException(Class<? extends Entity> clazz1, Class<? extends Entity> clazz2) {
        super(clazz1 + " is not consistent with " + clazz2);
    }
}
