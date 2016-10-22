package fr.enssat.lanniontech.api.entities.geojson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties({"_id"}) // "_id" is auto set by MongoDB
public class Feature extends GeoJsonObject {

    //  private static final String type = "Feature"; // case sensitive
    @JsonIgnore // Set in the 'properties'
    private UUID uuid = UUID.randomUUID();
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> properties = new HashMap<>();
    @JsonInclude(Include.ALWAYS)
    private GeoJsonObject geometry;
    @JsonProperty("id")
    private String openStreetMapID;

    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public GeoJsonObject getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonObject geometry) {
        this.geometry = geometry;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @JsonIgnore
    public String getId() {
        return openStreetMapID;
    }

    @JsonProperty("id")
    public void setId(String openStreetMapID) {
        this.openStreetMapID = openStreetMapID;
    }

    @Override
    public String toString() {
        return "Feature{properties=" + properties + ", geometry=" + geometry + ", id='" + uuid + "'}";
    }

    // public String getType() {
    //    return type;
    // }

}
