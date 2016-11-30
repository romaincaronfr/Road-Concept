package fr.enssat.lanniontech.core.positioning;

import fr.enssat.lanniontech.core.Tools;

public class Position {

    private Double latitude;
    private Double longitude;

    public Position(double longitude, double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static double length(Position A, Position B) {
        double dY = A.getLatitude() - B.getLatitude();
        double dX = (A.getLongitude() - B.getLongitude()) * Math.cos(((A.getLatitude() + B.getLatitude()) / 2) * Math.PI / 180);
        return Tools.round(Math.sqrt(dX * dX + dY * dY) * 1000 * 40000 / 360, 1);
    }

    public String toString() {
        return "Longitude: " + longitude + " , Latitude: " + latitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean equals(Object pos) {
        return pos instanceof Position && equals((Position) pos);
    }

    public boolean equals(Position pos) {
        return latitude.equals(pos.getLatitude()) && longitude.equals(pos.getLongitude());
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
