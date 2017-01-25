package fr.enssat.lanniontech.roadconceptandroid.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Coordinates {

    @SerializedName("longitude")
    @Expose
    private double longitude;

    @SerializedName("latitude")
    @Expose
    private double latitude;

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
}
