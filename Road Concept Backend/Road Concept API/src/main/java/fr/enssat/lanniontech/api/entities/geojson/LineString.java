package fr.enssat.lanniontech.api.entities.geojson;

public class LineString extends Geometry<Coordinates> {

    public boolean contains(Coordinates point) {
        return getCoordinates().contains(point);
    }

    public boolean isFirstOrLast(Coordinates point) {
        int index = getCoordinates().indexOf(point);
        return index == 0 || index == getCoordinates().size() - 1;
    }

    @Override
    public String toString() {
        return "LineString{} " + super.toString();
    }
}
