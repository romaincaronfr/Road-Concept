package fr.enssat.lanniontech.roadconceptandroid.Entities;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Iterator;
import java.util.List;

public class FeatureCollection implements Iterable<Feature> {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("features")
    @Expose
    private List<Feature> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    @Override
    public Iterator<Feature> iterator() {
        return getFeatures().iterator();
    }
}