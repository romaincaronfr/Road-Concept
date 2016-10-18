package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.*;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;

public class AdvancedTrajectory extends Trajectory {
    private Lane sourceLane;
    private Lane destinationLane;
    private ArrayList<PosFunction> Pfs;
    private ArrayList<Double> Ps;
    private double length;

    public AdvancedTrajectory(Lane source, Lane destination) {
        this.sourceLane = source;
        this.destinationLane = destination;

        Pfs = new ArrayList<PosFunction>();
        Ps = new ArrayList<Double>();

        Pfs.add(destination.getMyRoadSection().getFunction());

        Ps.add(source.getLength() - 10);
        double[] p = Pfs.get(0).getInterPos(Pfs.get(1), source.getMyWPos(), destination.getMyWPos());
        Ps.add(p[0]);
        Ps.add(p[1]);
        Ps.add(10.0);
        length = Ps.get(1) - Ps.get(0) + Ps.get(3) - Ps.get(2);
    }

    public double getP(int i) {
        return Ps.get(i);
    }

    public int getSectionSize() {
        return Pfs.size();
    }

    public Lane getDestination() {
        return destinationLane;
    }

    //todo delete all line under this comment

    @Override
    public Trajectory getNext() {
        return null;
    }

    @Override
    public double getNextCarSpeed(Side side) {
        return 0;
    }

    @Override
    public double getSpeedOfFirst() {
        return 0;
    }

    @Override
    public double getDistanceToNext(Side side) {
        return 0;
    }

    @Override
    public double getDistanceToFirst() {
        return 0;
    }

    @Override
    public boolean rangeIsFree(double start, double end) {
        return false;
    }

    @Override
    public Position getGPS(double pos) {
        return null;
    }
}
