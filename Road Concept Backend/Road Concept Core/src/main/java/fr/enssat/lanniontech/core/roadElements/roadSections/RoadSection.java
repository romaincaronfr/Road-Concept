package fr.enssat.lanniontech.core.roadElements.roadSections;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Congestion;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roads.Road;

public abstract class RoadSection {
    protected Position A;
    protected Position B;
    protected double length;
    private PosFunction function;
    private Road myRoad;

    public RoadSection(Position A, Position B, Road myRoad) {
        this.A = A;
        this.B = B;
        this.myRoad = myRoad;
        length = Position.length(A, B);
        function = new PosFunction(A, B);
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

    public Road getMyRoad() {
        return myRoad;
    }

    public abstract Congestion[] getCongestion();

    public abstract Lane getInputLane(Position P);

    public abstract Lane getOutputLane(Position P);
}
