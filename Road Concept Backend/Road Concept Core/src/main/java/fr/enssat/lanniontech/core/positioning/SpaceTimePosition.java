package fr.enssat.lanniontech.core.positioning;

import fr.enssat.lanniontech.core.vehicleElements.VehicleType;

public class SpaceTimePosition extends Position {

    private final int time;
    private final double angle;
    private final int vehicleId;
    private final VehicleType type;

    //todo reset to private
    private SpaceTimePosition(double lon, double lat, int time, double angle, int vehicleId,VehicleType type) {
        super(lon, lat);

        this.vehicleId = vehicleId;
        this.time = time;
        this.angle = angle;
        this.type = type;
    }

    public static SpaceTimePosition getMean(Position A, Position B, int time, int vehicleId, VehicleType type) {

        double lon = (A.lon + B.lon) / 2;
        double lat = (A.lat + B.lat) / 2;

        //todo compute angle from A and B
        return new SpaceTimePosition(lon, lat, time, 0, vehicleId,type);
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

    public VehicleType getType() {
        return type;
    }
}
