package fr.enssat.lanniontech.core.positioning;

import fr.enssat.lanniontech.core.roadElements.Lane;

import java.util.ArrayList;

public class Trajectory {
    private Lane source;
    private Lane destination;
    private ArrayList<PosFunction> Pfs;
    private ArrayList<Double> Ps;
    private double length;

    public Trajectory(Lane source, Lane destination) {
        this.source = source;
        this.destination = destination;

        Pfs = new ArrayList<PosFunction>();
        Ps = new ArrayList<Double>();

        Pfs.add(source.getMyRoadSection().getFunction());
        Pfs.add(destination.getMyRoadSection().getFunction());

        Ps.add(source.getLength() - 10);
        double[] p = Pfs.get(0).getInterPos(Pfs.get(1), source.getMyWPos(), destination.getMyWPos());
        Ps.add(p[0]);
        Ps.add(p[1]);
        Ps.add(10.0);
        length = Ps.get(1) - Ps.get(0) + Ps.get(3) - Ps.get(2);
    }
}
