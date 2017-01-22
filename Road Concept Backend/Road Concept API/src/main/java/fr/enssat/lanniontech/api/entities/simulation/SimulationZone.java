package fr.enssat.lanniontech.api.entities.simulation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class SimulationZone {

    @JsonProperty("working_feature")
    private UUID workingFeatureUUID;
    @JsonProperty("living_feature")
    private UUID livingFeatureUUID;
    @JsonProperty("car_percentage")
    private int carPercentage;
    @JsonProperty("vehicle_count")
    private int vehicleCount;
    @JsonProperty("departure_living_s")
    private int departureLivingS;
    @JsonProperty("departure_working_s")
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
