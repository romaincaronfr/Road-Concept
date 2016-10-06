package fr.enssat.lanniontech.api.jsonparser.old.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MapElementType {

    SIMPLE_ROAD(1), DOUBLE_ROAD(2), TRIPLE_ROAD(3), ROUNDABOUT(4), RED_LIGHT(5);

    private final int jsonID;

    MapElementType(int jsonID) {
        this.jsonID = jsonID;
    }

    @JsonCreator
    public static MapElementType forValue(int id) {
        for (MapElementType type : MapElementType.values()) {
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
