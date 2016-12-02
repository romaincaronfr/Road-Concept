package fr.enssat.lanniontech.api.entities.simulation;

import fr.enssat.lanniontech.api.entities.Entity;

import java.util.UUID;

public class SimulationCongestionResult implements Entity {

    private UUID featureUUID;
    private int congestionPercentage;

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

}
