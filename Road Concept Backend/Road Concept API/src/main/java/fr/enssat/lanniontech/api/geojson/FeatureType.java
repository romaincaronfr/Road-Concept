package fr.enssat.lanniontech.api.geojson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FeatureType {
    SINLGE_ROAD(1), DOUBLE_ROAD(2), TRIPLE_ROAD(3), ROUNDABOUT(4), RED_LIGHT(5);

    private final int jsonID;

    FeatureType(int jsonID) {
        this.jsonID = jsonID;
    }

    @JsonCreator
    public static FeatureType forValue(int id) {
        for (FeatureType type : FeatureType.values()) {
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
