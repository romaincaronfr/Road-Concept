package fr.enssat.lanniontech.api.entities.geojson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.enssat.lanniontech.api.utilities.jackson.CoordinatesDeserializer;
import fr.enssat.lanniontech.api.utilities.jackson.CoordinatesSerializer;

import java.io.Serializable;

@JsonDeserialize(using = CoordinatesDeserializer.class)
@JsonSerialize(using = CoordinatesSerializer.class)
public class Coordinates implements Serializable {

    private double longitude;
    private double latitude;

    public Coordinates() {
    }

    public Coordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "longitude=" + longitude + ", latitude=" + latitude + ", altitude=" + '}';
    }
}
