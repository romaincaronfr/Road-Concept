package fr.enssat.lanniontech.entities;

public class Properties {

    private MapObjectType type;
    private Integer maxSpeed; // may be null
    private String name;
    private boolean bridge;
    private Integer redLightTimeS; // may be null
    private Integer roundaboutLanes; // may be null

    public Properties() {
    }

    public MapObjectType getType() {
        return type;
    }

    public void setType(MapObjectType type) {
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

    public Integer getRedLightTimeS() {
        if (type != MapObjectType.FEU_ROUGE) {
            throw new IllegalArgumentException("Uniquement dans le cas d'un feu rouge !");
        }
        return redLightTimeS;
    }

    public void setRedLightTimeS(Integer redLightTimeS) {
        if (type != MapObjectType.FEU_ROUGE) {
            throw new IllegalArgumentException("Uniquement dans le cas d'un feu rouge !");
        }
        this.redLightTimeS = redLightTimeS;
    }

    public Integer getRoundaboutLanes() {
        if (type != MapObjectType.CARREFOUR_GIRATOIRE) {
            throw new IllegalArgumentException("Uniquement dans le cas d'un carrefour giratoire !");
        }
        return roundaboutLanes;
    }

    public void setRoundaboutLanes(Integer roundaboutLanes) {
        if (type != MapObjectType.CARREFOUR_GIRATOIRE) {
            throw new IllegalArgumentException("Uniquement dans le cas d'un carrefour giratoire !");
        }
        this.roundaboutLanes = roundaboutLanes;
    }
}
