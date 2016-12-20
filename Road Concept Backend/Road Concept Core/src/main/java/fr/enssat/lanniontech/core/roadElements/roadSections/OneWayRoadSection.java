package fr.enssat.lanniontech.core.roadElements.roadSections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roads.Road;

public class OneWayRoadSection extends RoadSection {

    private Lane lane;

    public OneWayRoadSection(Position A, Position B, Road myRoad){
        super(A, B, myRoad);
        lane = new Lane(this,0);
    }

    @Override
    public double getCongestion() {
        return lane.getCongestion();
    }
}
