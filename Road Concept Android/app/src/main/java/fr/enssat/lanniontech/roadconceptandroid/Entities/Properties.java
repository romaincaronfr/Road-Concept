package fr.enssat.lanniontech.roadconceptandroid.Entities;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Properties {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("congestion")
    @Expose
    private String congestion;

    public UUID getId() {
        return UUID.fromString(id);
    }

    public void setId(UUID uuid) {
        this.id = uuid.toString();
    }

    public String getCongestion() {
        return congestion;
    }

    public void setCongestion(String congestion) {
        this.congestion = congestion;
    }
}