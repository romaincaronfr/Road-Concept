package fr.enssat.lanniontech.core.roadElements.roadSections;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roads.Road;

public class DualWayRoadSection extends RoadSection {

    private Lane laneAB; //this lane is on rigth side in A -> B scenario
    private Lane laneBA; //this lane is on left side in A -> B scenario

    public DualWayRoadSection(Position A, Position B, Road myRoad) {
        super(A, B, myRoad);
        laneAB = new Lane(this, length);
        laneBA = new Lane(this, -length);
    }

    @Override
    public Lane getInputLane(Position P) {
        if (P == A) {
            return laneAB;
        } else {
            return laneBA;
        }
    }

    @Override
    public Lane getOutputLane(Position P) {
        if (P == B) {
            return laneAB;
        } else {
            return laneBA;
        }
    }

    public Lane getLaneAB() {
        return laneAB;
    }

    public Lane getLaneBA() {
        return laneBA;
    }

    @Override
    public double getCongestion() {
        return Math.max(laneAB.getCongestion(), laneBA.getCongestion());
    }
}
