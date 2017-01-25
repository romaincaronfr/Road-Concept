package fr.enssat.lanniontech.roadconceptandroid.Entities;

/**
 * Created by Romain on 22/01/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("maxspeed")
    @Expose
    private Integer maxspeed;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("bridge")
    @Expose
    private Boolean bridge;
    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("oneway")
    @Expose
    private String oneway;
    @SerializedName("congestion")
    @Expose
    private String congestion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxspeed() {
        return maxspeed;
    }

    public void setMaxspeed(Integer maxspeed) {
        this.maxspeed = maxspeed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getBridge() {
        return bridge;
    }

    public void setBridge(Boolean bridge) {
        this.bridge = bridge;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getOneway() {
        return oneway;
    }

    public void setOneway(String oneway) {
        this.oneway = oneway;
    }

    public String getCongestion() {
        return congestion;
    }

    public void setCongestion(String congestion) {
        this.congestion = congestion;
    }
}