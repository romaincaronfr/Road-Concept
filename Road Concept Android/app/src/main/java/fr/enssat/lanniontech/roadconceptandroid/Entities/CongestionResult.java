package fr.enssat.lanniontech.roadconceptandroid.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CongestionResult {

    @SerializedName("featureUUID")
    @Expose
    private String featureUUID;
    @SerializedName("congestionPercentage")
    @Expose
    private Integer congestionPercentage;
    @SerializedName("timestamp")
    @Expose
    private Integer timestamp;

    public String getFeatureUUID() {
        return featureUUID;
    }

    public void setFeatureUUID(String featureUUID) {
        this.featureUUID = featureUUID;
    }

    public Integer getCongestionPercentage() {
        return congestionPercentage;
    }

    public void setCongestionPercentage(Integer congestionPercentage) {
        this.congestionPercentage = congestionPercentage;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

}