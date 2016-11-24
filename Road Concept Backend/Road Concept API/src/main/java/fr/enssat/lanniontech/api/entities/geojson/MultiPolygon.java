package fr.enssat.lanniontech.api.entities.geojson;

import java.util.List;

@Deprecated
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
