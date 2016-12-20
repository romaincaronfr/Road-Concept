package fr.enssat.lanniontech.core.roadElements.roads;

import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.OneWayRoadSection;

import java.util.UUID;

public class OneWayRoad extends Road{

    public OneWayRoad(UUID id, int maxSpeed){
        super(id,maxSpeed,true);
    }

    public OneWayRoadSection get(int i) {
        return (OneWayRoadSection) sections.get(i);
    }
}
