package fr.enssat.lanniontech.api.entities.geojson;

import java.util.Collections;
import java.util.LinkedList;

public abstract class Geometry<T> extends GeoJsonObject {

    private LinkedList<T> coordinates = new LinkedList<T>(); // Need to keep order

    @SafeVarargs
    public Geometry(T... elements) {
        Collections.addAll(coordinates, elements);
    }

    public Geometry<T> add(T elements) {
        coordinates.add(elements);
        return this;
    }

    public LinkedList<T> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LinkedList<T> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "Geometry{" + "coordinates=" + coordinates + "} " + super.toString();
    }
}
