package fr.enssat.lanniontech.api.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PropertyItem {

    // @formatter:off
    PROPERTY_TYPE("type"),
    PROPERTY_BRIDGE("bridge"),
    PROPERTY_MAX_SPEED("maxSpeed"),
    PROPERTY_ROUNDABOUT_LANES("roundaboutLanes"),
    PROPERTY_NAME("name"),
    PROPERTY_REDLIGHT_TIME("redLightTime");
    // @formatter:on

    private String value;

    PropertyItem(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PropertyItem forValue(String value) {
        for (PropertyItem item : PropertyItem.values()) {
            if (item.getValue().equals(value)) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
