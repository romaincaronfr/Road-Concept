package fr.enssat.lanniontech.api.jsonparser;

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
