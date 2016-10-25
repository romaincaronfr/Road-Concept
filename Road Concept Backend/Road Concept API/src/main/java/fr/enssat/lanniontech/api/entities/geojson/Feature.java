package fr.enssat.lanniontech.api.entities.geojson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties({"_id"}) // "_id" is auto set by MongoDB
public class Feature extends GeoJsonObject {

    @JsonProperty(access = Access.WRITE_ONLY)
    private UUID uuid = UUID.randomUUID(); //FIXME: Change sur un update par exemple... Problème de unmarshalling non prioritaire
    @JsonInclude(Include.NON_NULL)
    private Map<String, Object> properties = new HashMap<>();
    @JsonInclude(Include.ALWAYS)
    private GeoJsonObject geometry;
    @JsonProperty(value = "id", access = Access.WRITE_ONLY)
    private String openStreetMapID;

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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getOpenStreetMapID() {
        return openStreetMapID;
    }

    public void setOpenStreetMapID(String openStreetMapID) {
        this.openStreetMapID = openStreetMapID;
    }

    @Override
    public String toString() {
        return "Feature{properties=" + properties + ", geometry=" + geometry + ", uuid='" + uuid + "'}";
    }

}
