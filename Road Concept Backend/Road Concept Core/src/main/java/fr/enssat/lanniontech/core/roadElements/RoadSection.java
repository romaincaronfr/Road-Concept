package fr.enssat.lanniontech.core.roadElements;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;

public class RoadSection {
    private Position A;
    private Position B;
    private double length;


    private Lane laneAB; //this lane is on rigth side in A -> B scenario
    private Lane laneBA; //this lane is on left side in A -> B scenario
    private PosFunction function;
    private Road myRoad;


    @Deprecated
    public RoadSection(Position A, Position B) {
        this(A, B, null);
    }

    public RoadSection(Position A, Position B, Road myRoad) {
        this.A = A;
        this.B = B;
        length = Position.length(A, B);
        function = new PosFunction(A, B);
        laneAB = new Lane(this, length);
        laneBA = new Lane(this, length);
        this.myRoad = myRoad;
    }

    public Position getPosition(Lane myLane, double pos) {
        if (myLane == laneAB) {
            return function.get(pos);
        } else {
            return function.get(length - pos);
        }
    }

    /**
     * @param myLane
     * @param pos
     * @param widthPos position of the vehicle from the left side of the road
     * @return
     */
    public Position getPosition(Lane myLane, double pos, double widthPos) {
        if (myLane == laneAB) {
            return function.get(pos, widthPos);
        } else {
            return function.get(length - pos, -widthPos);
        }
    }

    public Lane getRightLane(Position P) {
        if (P == A) {
            return laneAB;
        } else {
            return laneBA;
        }
    }

    public Lane getLeftLane(Position P) {
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

    public double getLength() {
        return length;
    }

    public Position getA() {
        return A;
    }

    public Position getB() {
        return B;
    }

    public PosFunction getFunction() {
        return function;
    }

    public double getWPos(Lane lane, double width) {
        if (lane == laneAB) {
            return width / 2;
        } else {
            return -width / 2;
        }
    }

    public Road getMyRoad() {
        return myRoad;
    }

    public boolean isLeftLane(Lane l) {
        return laneBA == l;
    }
}
