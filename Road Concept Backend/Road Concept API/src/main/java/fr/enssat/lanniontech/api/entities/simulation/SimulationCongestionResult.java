package fr.enssat.lanniontech.api.entities.simulation;

import fr.enssat.lanniontech.api.entities.Entity;

import java.util.UUID;

public class SimulationCongestionResult implements Entity {

    private UUID featureUUID;
    private int congestionPercentage;
    private int timestamp;

    public SimulationCongestionResult(UUID featureUUID, int congestionPercentage, int timestamp) {
        this.featureUUID = featureUUID;
        this.congestionPercentage = congestionPercentage;
        this.timestamp = timestamp;
    }

    public SimulationCongestionResult() {

    }

    public UUID getFeatureUUID() {
        return featureUUID;
    }

    public void setFeatureUUID(UUID featureUUID) {
        this.featureUUID = featureUUID;
    }

    public int getCongestionPercentage() {
        return congestionPercentage;
    }

    public void setCongestionPercentage(int congestionPercentage) {
        this.congestionPercentage = congestionPercentage;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
