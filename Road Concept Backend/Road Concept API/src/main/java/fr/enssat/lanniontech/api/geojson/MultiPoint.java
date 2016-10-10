package fr.enssat.lanniontech.api.geojson;

public class MultiPoint extends Geometry<Coordinates> {

    public MultiPoint() {
    }

    public MultiPoint(Coordinates... points) {
        super(points);
    }

    @Override
    public String toString() {
        return "MultiPoint{} " + super.toString();
    }
}
