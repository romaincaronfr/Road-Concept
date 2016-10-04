package fr.enssat.lanniontech.core.positioning;

import fr.enssat.lanniontech.core.roadElements.Lane;

import java.util.ArrayList;

public class Trajectory {
    private ArrayList<PosFunction> Pfs;
    private ArrayList<Double> Ps;

    public Trajectory(Lane Source, Lane Destination) {
        Pfs = new ArrayList<PosFunction>();
        Ps = new ArrayList<Double>();

        Pfs.add(Source.getMyRoadSection().getFunction());
        Pfs.add(Destination.getMyRoadSection().getFunction());

        Ps.add(Source.getLength() - 10);
        double[] p = Pfs.get(0).getInterPos(Pfs.get(1), Source.getMyWPos(), Destination.getMyWPos());
        Ps.add(p[0]);
        Ps.add(p[1]);
        Ps.add(10.0);
    }
}
