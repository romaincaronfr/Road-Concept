package fr.enssat.lanniontech.core.roadElements.roads;

import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;

import java.util.UUID;

public class DualWayRoad extends Road {

    public DualWayRoad(UUID id, int maxSpeed) {
        super(id, maxSpeed, false);
    }

    public DualWayRoadSection get(int i) {
        return (DualWayRoadSection) sections.get(i);
    }

    @Override
    public int getCongestion() {
        double occupiedSpace1 = 0;
        double occupiedSpace2 = 0;
        for (RoadSection rs : sections) {
            occupiedSpace1 += rs.getCongestion()[0].getCongestionValue();
            occupiedSpace2 += rs.getCongestion()[1].getCongestionValue();
        }
        double occupiedSpace = Math.max(occupiedSpace1, occupiedSpace2);
        return (int) (100 * occupiedSpace / getLength());
    }

}
