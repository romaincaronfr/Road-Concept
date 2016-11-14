package fr.enssat.lanniontech.api.entities.geojson;

public class LineString extends MultiPoint {

    public LineString() {
    }

    public LineString(Coordinates... points) {
        super(points);
    }

    public boolean contains(Coordinates point){
        return getCoordinates().contains(point);
    }

    public boolean isFirstOrLast(Coordinates point){
        return getCoordinates().getFirst().equals(point) ||
                getCoordinates().getLast().equals(point);
    }

    @Override
    public String toString() {
        return "LineString{} " + super.toString();
    }
}
