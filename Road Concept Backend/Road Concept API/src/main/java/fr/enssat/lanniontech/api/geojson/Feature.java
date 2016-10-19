package fr.enssat.lanniontech.api.geojson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties({"id"})
public class Feature extends GeoJsonObject {

    private static final String type = "feature";
    @JsonInclude(Include.ALWAYS)
    private Integer id; //TODO: Voir pour récupérer l'ID qui est affecté par MongoDB ?
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> properties = new HashMap<>();
    @JsonInclude(Include.ALWAYS)
    private GeoJsonObject geometry;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Feature{properties=" + properties + ", geometry=" + geometry + ", id='" + id + "'}";
    }

    public String getType() {
        return type;
    }

}
