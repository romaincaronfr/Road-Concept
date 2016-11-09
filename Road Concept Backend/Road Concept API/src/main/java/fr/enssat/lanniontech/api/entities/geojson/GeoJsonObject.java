package fr.enssat.lanniontech.api.entities.geojson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.io.Serializable;

@JsonTypeInfo(property = "type", use = Id.NAME)
@JsonSubTypes({@Type(Feature.class), @Type(FeatureCollection.class), @Type(Point.class), @Type(Polygon.class), @Type(MultiPolygon.class), @Type(MultiPoint.class), @Type(LineString.class)})
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({"_id", "crs", "bbox"})
public class GeoJsonObject implements Serializable {

    @Override
    public String toString() {
        return "GeoJsonObject{}";
    }
}
