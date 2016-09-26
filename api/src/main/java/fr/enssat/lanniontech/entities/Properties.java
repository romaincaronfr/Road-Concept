package fr.enssat.lanniontech.entities;

/**
 * Created by Romain on 26/09/2016.
 */
public class Properties {

    private TypeEnum type;
    private int maxSpeed;
    private String name;
    private boolean bridge;
    private int redLightTime;
    private int roundaboutLanes;

    public Properties() {
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBridge() {
        return bridge;
    }

    public void setBridge(boolean bridge) {
        this.bridge = bridge;
    }

    public int getRedLightTime() {
        return redLightTime;
    }

    public void setRedLightTime(int redLightTime) {
        this.redLightTime = redLightTime;
    }

    public int getRoundaboutLanes() {
        return roundaboutLanes;
    }

    public void setRoundaboutLanes(int roundaboutLanes) {
        this.roundaboutLanes = roundaboutLanes;
    }
}
