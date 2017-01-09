package fr.enssat.lanniontech.core.roadElements.roads;

import fr.enssat.lanniontech.core.roadElements.roadSections.OneWayRoadSection;

import java.util.UUID;

public class RoundAbout extends Road {

    public RoundAbout(UUID id) {
        super(id, 30, true);
    }

    public OneWayRoadSection get(int i) {
        return (OneWayRoadSection) sections.get(i);
    }
}
