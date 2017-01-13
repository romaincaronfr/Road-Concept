package fr.enssat.lanniontech.roadconceptandroid.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
    @SerializedName("samplingRate")
    @Expose
    private Integer samplingRate;
    @SerializedName("finish")
    @Expose
    private Boolean finish;
    @SerializedName("livingFeatureUUID")
    @Expose
    private String livingFeatureUUID;
    @SerializedName("workingFeatureUUID")
    @Expose
    private String workingFeatureUUID;
    @SerializedName("departureLivingS")
    @Expose
    private Integer departureLivingS;
    @SerializedName("departureWorkingS")
    @Expose
    private Integer departureWorkingS;
    @SerializedName("carPercentage")
    @Expose
    private Integer carPercentage;
    @SerializedName("vehicleCount")
    @Expose
    private Integer vehicleCount;

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

    public Integer getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(Integer samplingRate) {
        this.samplingRate = samplingRate;
    }

    public Boolean getFinish() {
        return finish;
    }

    public void setFinish(Boolean finish) {
        this.finish = finish;
    }

    public String getLivingFeatureUUID() {
        return livingFeatureUUID;
    }

    public void setLivingFeatureUUID(String livingFeatureUUID) {
        this.livingFeatureUUID = livingFeatureUUID;
    }

    public String getWorkingFeatureUUID() {
        return workingFeatureUUID;
    }

    public void setWorkingFeatureUUID(String workingFeatureUUID) {
        this.workingFeatureUUID = workingFeatureUUID;
    }

    public Integer getDepartureLivingS() {
        return departureLivingS;
    }

    public void setDepartureLivingS(Integer departureLivingS) {
        this.departureLivingS = departureLivingS;
    }

    public Integer getDepartureWorkingS() {
        return departureWorkingS;
    }

    public void setDepartureWorkingS(Integer departureWorkingS) {
        this.departureWorkingS = departureWorkingS;
    }

    public Integer getCarPercentage() {
        return carPercentage;
    }

    public void setCarPercentage(Integer carPercentage) {
        this.carPercentage = carPercentage;
    }

    public Integer getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Integer vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", creatorID=" + creatorID +
                ", mapID=" + mapID +
                ", creationDate='" + creationDate + '\'' +
                ", samplingRate=" + samplingRate +
                ", finish=" + finish +
                ", livingFeatureUUID='" + livingFeatureUUID + '\'' +
                ", workingFeatureUUID='" + workingFeatureUUID + '\'' +
                ", departureLivingS=" + departureLivingS +
                ", departureWorkingS=" + departureWorkingS +
                ", carPercentage=" + carPercentage +
                ", vehicleCount=" + vehicleCount +
                '}';
    }
}