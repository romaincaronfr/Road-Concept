package fr.enssat.lanniontech.api.entities.simulation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.enssat.lanniontech.api.entities.SQLEntity;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.exceptions.SimulatorUnavailableException;
import fr.enssat.lanniontech.core.Simulator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Simulation implements SQLEntity {

    @JsonIgnore
    private Simulator simulator;

    private UUID uuid = UUID.randomUUID();
    private String name;
    private int creatorID;
    private int mapID; //TODO: Remove it when the FrontEnd is ready to change...
    private MapInfo mapInfo;
    private String creationDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    @JsonProperty("sampling_rate")
    private int samplingRate;
    private boolean finish;
    @JsonProperty("random_traffic")
    private boolean includeRandomTraffic;
   // @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int departureLivingS; // a garder (prendre le min)
    private List<SimulationZone> zones = new ArrayList<>();

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

    public int getDepartureLivingS() {
        return departureLivingS;
    }

    public void setDepartureLivingS(int departureLivingS) {
        this.departureLivingS = departureLivingS;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }

    public Simulator getSimulator() {
        if (!finish) {
            return simulator;
        }
        throw new SimulatorUnavailableException("Simulation finished");
    }

    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }

    public List<SimulationZone> getZones() {
        return zones;
    }

    public void setZones(List<SimulationZone> zones) {
        this.zones = zones;
    }

    public boolean isIncludeRandomTraffic() {
        return includeRandomTraffic;
    }

    public void setIncludeRandomTraffic(boolean includeRandomTraffic) {
        this.includeRandomTraffic = includeRandomTraffic;
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
