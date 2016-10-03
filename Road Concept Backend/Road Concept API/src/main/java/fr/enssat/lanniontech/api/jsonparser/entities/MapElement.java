package fr.enssat.lanniontech.api.jsonparser.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MapElement {

    private int mapID;
    @JsonProperty(value = "objectID")
    private int id;
    private Properties properties;
    private Geometry geometry;

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
