package fr.enssat.lanniontech.api.entities.geojson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FeatureCollection extends GeoJsonObject implements Iterable<Feature> {

    private List<Feature> features = new ArrayList<>();

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    @Override
    public Iterator<Feature> iterator() {
        return features.iterator();
    }

    @Override
    public String toString() {
        return "FeatureCollection{" + "features=" + features + '}';
    }
}
