package fr.enssat.lanniontech.jsonparser.entities;

public class Properties {

    private MapElementType type;
    private boolean bridge;
    private String name = ""; // Avoid null
    private Integer maxSpeed; // May be null
    private Integer roundaboutLanes; // May be null
    private Integer redLightTime; // May be null

    public Integer getRoundaboutLanes() {
        if (type != MapElementType.ROUNDABOUT) {
            throw new IllegalArgumentException("Disponible uniquement pour les carrefour giratoires");
        }
        return roundaboutLanes;
    }

    public void setRoundaboutLanes(Integer roundaboutLanes) {
        if (type != MapElementType.ROUNDABOUT) {
            throw new IllegalArgumentException("Disponible uniquement pour les carrefour giratoires");
        }
        this.roundaboutLanes = roundaboutLanes;
    }

    public Integer getRedLightTime() {
        if (type != MapElementType.RED_LIGHT) {
            throw new IllegalArgumentException("Temps d'attente uniquement disponible pour un feu rouge.");
        }
        return redLightTime;
    }

    public void setRedLightTime(Integer redLightTime) {
        if (type != MapElementType.RED_LIGHT) {
            throw new IllegalArgumentException("Temps d'attente uniquement disponible pour un feu rouge.");
        }
        this.redLightTime = redLightTime;
    }

    public MapElementType getType() {
        return type;
    }

    public void setType(MapElementType type) {
        this.type = type;
    }

    public Integer getMaxSpeed() {
        if (type == MapElementType.RED_LIGHT) {
            throw new IllegalArgumentException("Pas de vitesse maximale sur un feu rouge");
        }
        return maxSpeed;
    }

    public void setMaxSpeed(Integer maxSpeed) {
        if (type == MapElementType.RED_LIGHT) {
            throw new IllegalArgumentException("Pas de vitesse maximale sur un feu rouge");
        }
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
