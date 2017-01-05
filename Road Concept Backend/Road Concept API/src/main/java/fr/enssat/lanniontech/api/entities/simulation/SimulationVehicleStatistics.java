package fr.enssat.lanniontech.api.entities.simulation;

import fr.enssat.lanniontech.api.entities.Entity;

public class SimulationVehicleStatistics implements Entity {

    private int vehicleID;
    private int averageSpeed;
    private int delayDueToCongestionS;

    public SimulationVehicleStatistics(int vehicleID, int averageSpeed, int delayDueToCongestionS) {
        this.vehicleID = vehicleID;
        this.averageSpeed = averageSpeed;
        this.delayDueToCongestionS = delayDueToCongestionS;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(int averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getDelayDueToCongestionS() {
        return delayDueToCongestionS;
    }

    public void setDelayDueToCongestionS(int delayDueToCongestionS) {
        this.delayDueToCongestionS = delayDueToCongestionS;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

}
