package fr.enssat.lanniontech.core.roadElements.roads;

import fr.enssat.lanniontech.core.roadElements.roadSections.OneWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;

import java.util.UUID;

public class OneWayRoad extends Road {

    public OneWayRoad(UUID id, int maxSpeed) {
        super(id, maxSpeed, true);
    }

    public OneWayRoadSection get(int i) {
        return (OneWayRoadSection) sections.get(i);
    }

    @Override
    public int getCongestion() {
        double occupiedSpace = 0;
        for (RoadSection rs : sections) {
            occupiedSpace += rs.getCongestion()[0].getCongestionValue();
        }
        return (int) (100 * occupiedSpace / getLength());
    }
}
