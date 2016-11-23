package fr.enssat.lanniontech.api.entities.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.enssat.lanniontech.api.entities.SQLEntity;
import fr.enssat.lanniontech.api.exceptions.SimulatorUnavailableException;
import fr.enssat.lanniontech.core.Simulator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Simulation implements SQLEntity {

    @JsonIgnore
    private final Simulator simulator = new Simulator(); // not stored
    private UUID uuid = UUID.randomUUID();
    private String name;
    private int creatorID;
    private int mapID;
    private String creationDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private int samplingRate;
    private boolean finish;
    private UUID livingFeatureUUID;
    private UUID workingFeatureUUID;
    private int departureS;
    private int arrivalS;
    private int carPercentage;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public UUID getLivingFeatureUUID() {
        return livingFeatureUUID;
    }

    public void setLivingFeatureUUID(UUID livingFeatureUUID) {
        this.livingFeatureUUID = livingFeatureUUID;
    }

    public UUID getWorkingFeatureUUID() {
        return workingFeatureUUID;
    }

    public void setWorkingFeatureUUID(UUID workingFeatureUUID) {
        this.workingFeatureUUID = workingFeatureUUID;
    }

    public int getDepartureS() {
        return departureS;
    }

    public void setDepartureS(int departureS) {
        this.departureS = departureS;
    }

    public int getArrivalS() {
        return arrivalS;
    }

    public void setArrivalS(int arrivalS) {
        this.arrivalS = arrivalS;
    }

    public int getCarPercentage() {
        return carPercentage;
    }

    public void setCarPercentage(int carPercentage) {
        this.carPercentage = carPercentage;
    }

    public Simulator getSimulator() throws SimulatorUnavailableException {
        if (!finish) {
            return simulator;
        }
        throw new SimulatorUnavailableException("Simulation finished");
    }

    @Override
    public Object getIdentifierValue() {
        return uuid;
    }

    @Override
    public String getIdentifierName() {
        return "uuid";
    }
}
