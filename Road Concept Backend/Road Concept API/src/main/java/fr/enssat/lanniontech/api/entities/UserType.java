package fr.enssat.lanniontech.api.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserType {
    USER(1),
    ADMINISTRATOR(2);

    private final int jsonID;

    UserType(int jsonID) {
        this.jsonID = jsonID;
    }

    @JsonCreator
    public static UserType forValue(int id) {
        for (UserType type : UserType.values()) {
            if (type.getJsonID() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + id);
    }

    @JsonValue
    public int getJsonID() {
        return jsonID;
    }
}
