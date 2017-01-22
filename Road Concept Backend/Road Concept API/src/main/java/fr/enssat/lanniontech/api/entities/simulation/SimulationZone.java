package fr.enssat.lanniontech.api.entities.simulation;

import java.util.UUID;

public class SimulationZone {

    private UUID workingFeatureUUID;
    private UUID livingFeatureUUID;
    private int carPercentage;
    private int vehicleCount;
    private int departureLivingS;
    private int departureWorkingS;

    public UUID getWorkingFeatureUUID() {
        return workingFeatureUUID;
    }

    public void setWorkingFeatureUUID(UUID workingFeatureUUID) {
        this.workingFeatureUUID = workingFeatureUUID;
    }

    public UUID getLivingFeatureUUID() {
        return livingFeatureUUID;
    }

    public void setLivingFeatureUUID(UUID livingFeatureUUID) {
        this.livingFeatureUUID = livingFeatureUUID;
    }

    public int getCarPercentage() {
        return carPercentage;
    }

    public void setCarPercentage(int carPercentage) {
        this.carPercentage = carPercentage;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(int vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public int getDepartureLivingS() {
        return departureLivingS;
    }

    public void setDepartureLivingS(int departureLivingS) {
        this.departureLivingS = departureLivingS;
    }

    public int getDepartureWorkingS() {
        return departureWorkingS;
    }

    public void setDepartureWorkingS(int departureWorkingS) {
        this.departureWorkingS = departureWorkingS;
    }
}
