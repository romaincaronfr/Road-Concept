package fr.enssat.lanniontech.api.entities.geojson;

import java.util.ArrayList;
import java.util.List;

public abstract class Geometry<T> extends GeoJsonObject {

    private List<T> coordinates = new ArrayList<T>(); //NOSONAR: List can be serialized without problem

    public Geometry<T> add(T elements) {
        coordinates.add(elements);
        return this;
    }

    public List<T> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<T> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Geometry{" + "coordinates=" + coordinates + "} " + super.toString();
    }
}
