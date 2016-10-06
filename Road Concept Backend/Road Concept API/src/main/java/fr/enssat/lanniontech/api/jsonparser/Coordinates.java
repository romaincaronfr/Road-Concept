package fr.enssat.lanniontech.api.jsonparser;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.enssat.lanniontech.api.jsonparser.jackson.LngLatAltDeserializer;
import fr.enssat.lanniontech.api.jsonparser.jackson.LngLatAltSerializer;

import java.io.Serializable;

@JsonDeserialize(using = LngLatAltDeserializer.class)
@JsonSerialize(using = LngLatAltSerializer.class)
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
