package fr.enssat.lanniontech.jsonparser.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MapElementType {

    SIMPLE_ROAD(1),
    DOUBLE_ROAD(2),
    TRIPLE_ROAD(3),
    ROUNDABOUT(4),
    RED_LIGHT(5);

    private final int jsonID;

    MapElementType(int jsonID) {
        this.jsonID = jsonID;
    }

    public int getJsonID() {
        return jsonID;
    }

    @JsonCreator
    public static MapElementType forValue( int id) {
        for (MapElementType type: MapElementType.values()) {
            if (type.getJsonID() == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + id);
    }
}
