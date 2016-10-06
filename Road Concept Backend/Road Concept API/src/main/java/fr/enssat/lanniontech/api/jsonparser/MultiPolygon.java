package fr.enssat.lanniontech.api.jsonparser;

import java.util.List;

public class MultiPolygon extends Geometry<List<List<Coordinates>>> {

    public MultiPolygon() {
    }

    public MultiPolygon(Polygon polygon) {
        add(polygon);
    }

    public MultiPolygon add(Polygon polygon) {
        getCoordinates().add(polygon.getCoordinates());
        return this;
    }

    @Override
    public String toString() {
        return "MultiPolygon{} " + super.toString();
    }
}
