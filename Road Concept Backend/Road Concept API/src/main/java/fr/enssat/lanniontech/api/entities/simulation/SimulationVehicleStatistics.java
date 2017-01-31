package fr.enssat.lanniontech.api.entities.simulation;

import fr.enssat.lanniontech.api.entities.Entity;

public class SimulationVehicleStatistics implements Entity {

    private int vehicleID;
    private int averageSpeed;
    private int distanceDone;
    private int duration;

    public SimulationVehicleStatistics(int vehicleID, int averageSpeed, int duration, int distanceDone) {
        this.vehicleID = vehicleID;
        this.averageSpeed = averageSpeed;
        this.distanceDone = distanceDone;
        this.duration = duration;
    }

    public SimulationVehicleStatistics() {
        // Needed by Jackson
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(int averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getDistanceDone() {
        return distanceDone;
    }

    public void setDistanceDone(int distanceDone) {
        this.distanceDone = distanceDone;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
