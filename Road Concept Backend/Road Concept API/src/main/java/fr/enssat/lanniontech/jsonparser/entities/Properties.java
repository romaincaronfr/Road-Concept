package fr.enssat.lanniontech.jsonparser.entities;

public class Properties {

    private MapElementType type;
    private boolean bridge;
    private String name = ""; // Avoid null
    private Integer maxSpeed; // May be null
    private Integer roundaboutLanes; // May be null
    private Integer redLightTime; // May be null

    public Integer getRoundaboutLanes() {
        return roundaboutLanes;
    }

    public void setRoundaboutLanes(Integer roundaboutLanes) {
        this.roundaboutLanes = roundaboutLanes;
    }

    public Integer getRedLightTime() {
        return redLightTime;
    }

    public void setRedLightTime(Integer redLightTime) {
        this.redLightTime = redLightTime;
    }

    public MapElementType getType() {
        return type;
    }

    public void setType(MapElementType type) {
        this.type = type;
    }

    public Integer getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
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
}
