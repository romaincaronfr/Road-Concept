package fr.enssat.lanniontech.roadconceptandroid.Entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Romain on 22/01/2017.
 */

public class InfosMap {

    @SerializedName("infos")
    @Expose
    private Map map;
    @SerializedName("features")
    @Expose
    private FeatureCollection featureCollection;

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public FeatureCollection getFeatureCollection() {
        return featureCollection;
    }

    public void setFeatureCollection(FeatureCollection featureCollection) {
        this.featureCollection = featureCollection;
    }
}
