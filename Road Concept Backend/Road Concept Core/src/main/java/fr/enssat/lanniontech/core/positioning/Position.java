package fr.enssat.lanniontech.core.positioning;

import fr.enssat.lanniontech.core.Tools;

public class Position {
    protected double lat;
    protected double lon;
    protected long time;

    public Position(double lat, double lon) {
        this(lat, lon, 0);
    }

    public Position(double lat, double lon, long time) {
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public static double length(Position A, Position B) {
        double dY = A.lat - B.lat;
        double dX = (A.lon - B.lon) * Math.cos(((A.lat + B.lat) / 2) * Math.PI / 180);
        return Tools.round(Math.sqrt(dX * dX + dY * dY) * 1000 * 40000 / 360, 1);
    }

    public void setTime(long time) {
        this.time = time;
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

    public boolean equals(Position pos) {
        return this.lat == pos.getLat() && this.lon == pos.getLon();
    }
}
