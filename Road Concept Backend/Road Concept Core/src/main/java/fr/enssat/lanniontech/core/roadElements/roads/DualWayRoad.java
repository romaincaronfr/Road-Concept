package fr.enssat.lanniontech.core.roadElements.roads;

import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;

import java.util.UUID;

public class DualWayRoad extends Road{

    public DualWayRoad(UUID id, int maxSpeed){
        super(id,maxSpeed,false);
    }

    public DualWayRoadSection get(int i) {
        return (DualWayRoadSection) sections.get(i);
    }

}
