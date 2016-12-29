package fr.enssat.lanniontech.core.roadElements.roadSections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roads.Road;

public class OneWayRoadSection extends RoadSection {

    private Lane lane;

    public OneWayRoadSection(Position A, Position B, Road myRoad){
        super(A, B, myRoad);
        lane = new Lane(this,length);
    }

    public Lane getLane() {
        return lane;
    }

    @Override
    public Lane getInputLane(Position P) {
        if(P == A){
            return lane;
        }
        return null;
    }

    @Override
    public Lane getOutputLane(Position P) {
        if(P == B){
            return lane;
        }
        return null;
    }

    @Override
    public double getCongestion() {
        return lane.getCongestion();
    }
}
