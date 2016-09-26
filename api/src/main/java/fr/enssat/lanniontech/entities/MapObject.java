package fr.enssat.lanniontech.entities;

import java.util.List;
import java.util.UUID;

public class MapObject {

    private UUID mapUUID;
    private UUID objectUUID;
    private Properties properties;
    private List<GPSPoints> geometry;

    public MapObject() {
    }

    public UUID getMapUUID() {
        return mapUUID;
    }

    public void setMapUUID(UUID mapUUID) {
        this.mapUUID = mapUUID;
    }

    public UUID getObjectUUID() {
        return objectUUID;
    }

    public void setObjectUUID(UUID objectUUID) {
        this.objectUUID = objectUUID;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<GPSPoints> getGeometry() {
        return geometry;
    }

    public void setGeometry(List<GPSPoints> geometry) {
        this.geometry = geometry;
    }
}
