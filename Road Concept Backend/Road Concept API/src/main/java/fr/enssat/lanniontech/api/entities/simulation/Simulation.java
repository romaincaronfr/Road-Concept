package fr.enssat.lanniontech.api.entities.simulation;

import fr.enssat.lanniontech.api.entities.User;

import java.util.UUID;

public class Simulation {

    private UUID uuid = UUID.randomUUID();
    private String name;
    private User user; // Creator of the simulation
    private int mapID;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
}
