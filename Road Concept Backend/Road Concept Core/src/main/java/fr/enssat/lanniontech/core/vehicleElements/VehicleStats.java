package fr.enssat.lanniontech.core.vehicleElements;

public class VehicleStats {

    private int id;
    private int averageSpeed;
    private int elapsedTime;
    private int distanceDone;

    public VehicleStats(int id, int averageSpeed, int elapsedTime, int distanceDone) {
        this.id = id;
        this.averageSpeed = averageSpeed;
        this.elapsedTime = elapsedTime;
        this.distanceDone = distanceDone;
    }

    public int getId() {
        return id;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getDistanceDone() {
        return distanceDone;
    }
}
