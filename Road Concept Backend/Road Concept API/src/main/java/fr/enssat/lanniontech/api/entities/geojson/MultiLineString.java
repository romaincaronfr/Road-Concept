package fr.enssat.lanniontech.api.entities.geojson;

import java.util.List;

public class MultiLineString extends Geometry<List<Coordinates>> {

    public MultiLineString() {
    }

    public MultiLineString(List<Coordinates> line) {
        add(line);
    }

    @Override
    public String toString() {
        return "MultiLineString{} " + super.toString();
    }
}