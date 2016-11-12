package fr.enssat.lanniontech.api.entities.simulation;

import fr.enssat.lanniontech.api.entities.SQLEntity;

import java.util.UUID;

public class Simulation implements SQLEntity{

    private UUID uuid = UUID.randomUUID();
    private String name;
    private int creatorID; // Creator of the simulation
    private int mapID;

    private long durationS;
    private boolean finish;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(int creatorID) {this.creatorID = creatorID;}

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

    public long getDurationS() {
        return durationS;
    }

    public void setDurationS(long durationS) {
        this.durationS = durationS;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
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
