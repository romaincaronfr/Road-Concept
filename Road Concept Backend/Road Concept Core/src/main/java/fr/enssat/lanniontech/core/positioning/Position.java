package fr.enssat.lanniontech.core.positioning;

import fr.enssat.lanniontech.core.Tools;

public class Position {
    protected double lat;
    protected double lon;

    public Position(double lon, double lat) {
        this.lat = lat;
        this.lon = lon;
    }

    public static double length(Position A, Position B) {
        double dY = A.lat - B.lat;
        double dX = (A.lon - B.lon) * Math.cos(((A.lat + B.lat) / 2) * Math.PI / 180);
        return Tools.round(Math.sqrt(dX * dX + dY * dY) * 1000 * 40000 / 360, 1);
    }

    public String toString() {
        return "Longitude: " + lon + " , Latitude: " + lat;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean equals(Object pos) {
        if (pos instanceof Position) {
            return equals((Position)pos);
        }else {
            return false;
        }
    }

    public boolean equals(Position pos){
        return this.lat == pos.getLat() && this.lon == pos.getLon();
    }

    static SpaceTimePosition getMean(Position A, Position B, long time){
        double lon = (A.lon+B.lon)/2;
        double lat = (A.lat+B.lat)/2;
        //todo compute angle from A and B
        return new SpaceTimePosition(lon,lat,time);
    }
}
