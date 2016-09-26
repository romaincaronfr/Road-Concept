package fr.enssat.lanniontech.entities;

import java.util.List;

/**
 * Created by Romain on 26/09/2016.
 */
public class MapObject {

    private Integer mapID;
    private Integer objectID;
    private Properties properties;
    private List<GPSPoints> geometry;

    public MapObject() {
    }

    public Integer getMapID() {
        return mapID;
    }

    public void setMapID(Integer mapID) {
        this.mapID = mapID;
    }

    public Integer getObjectID() {
        return objectID;
    }

    public void setObjectID(Integer objectID) {
        this.objectID = objectID;
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
