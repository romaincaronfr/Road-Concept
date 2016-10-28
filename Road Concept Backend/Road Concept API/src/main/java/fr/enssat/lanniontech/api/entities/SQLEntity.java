package fr.enssat.lanniontech.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SQLEntity extends Entity {

    @JsonIgnore
    Object getIdentifierValue();

    @JsonIgnore
    String getIdentifierName();
}
