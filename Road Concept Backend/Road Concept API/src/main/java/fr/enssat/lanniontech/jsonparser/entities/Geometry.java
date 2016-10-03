package fr.enssat.lanniontech.jsonparser.entities;


import java.util.List;

public class Geometry {

    private List<GPSPoint> coordinates;

    public List<GPSPoint> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<GPSPoint> coordinates) {
        this.coordinates = coordinates;
    }

}
