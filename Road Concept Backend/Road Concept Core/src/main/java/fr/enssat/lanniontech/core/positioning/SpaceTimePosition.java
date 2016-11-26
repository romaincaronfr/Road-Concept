package fr.enssat.lanniontech.core.positioning;

public class SpaceTimePosition extends Position {

    int time;
    double angle;
    int vehicleId;

    //todo reset to private
    public SpaceTimePosition(double lon, double lat, int time, double angle, int vehicleId) {
        super(lon, lat);

        this.vehicleId = vehicleId;
        this.time = time;
        this.angle = angle;

        //System.out.println("id : "+ this.vehicleId + " " + super.toString());
    }

    public static SpaceTimePosition getMean(Position A, Position B, int time, int vehicleId) {
        //System.out.println("avt: "+A);
        //System.out.println("arr: "+B);

        double lon = (A.lon + B.lon) / 2;
        double lat = (A.lat + B.lat) / 2;

        //todo compute angle from A and B
        return new SpaceTimePosition(lon, lat, time, 0, vehicleId);
    }

    public long getTime() {
        return time;
    }

    public int getId() {
        return vehicleId;
    }

    public double getAngle() {
        return angle;
    }
}
