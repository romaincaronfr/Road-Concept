package fr.enssat.lanniontech.roadconceptandroid.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Simulation {

    @SerializedName("uuid")
    @Expose
    private String uuid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("creatorID")
    @Expose
    private Integer creatorID;
    @SerializedName("mapID")
    @Expose
    private Integer mapID;
    @SerializedName("creationDate")
    @Expose
    private String creationDate;
    @SerializedName("finish")
    @Expose
    private Boolean finish;
    @SerializedName("departureLivingS")
    @Expose
    private Integer departureLivingS;
    @SerializedName("zones")
    @Expose
    private List<Zone> zones = null;
    @SerializedName("samplingRate")
    @Expose
    private Integer samplingRate;
    @SerializedName("random_traffic")
    @Expose
    private Boolean randomTraffic;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public Integer getMapID() {
        return mapID;
    }

    public void setMapID(Integer mapID) {
        this.mapID = mapID;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getFinish() {
        return finish;
    }

    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    public Integer getDepartureLivingS() {
        return departureLivingS;
    }

    public void setDepartureLivingS(Integer departureLivingS) {
        this.departureLivingS = departureLivingS;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public Integer getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(Integer samplingRate) {
        this.samplingRate = samplingRate;
    }

    public Boolean getRandomTraffic() {
        return randomTraffic;
    }

    public void setRandomTraffic(Boolean randomTraffic) {
        this.randomTraffic = randomTraffic;
    }

}