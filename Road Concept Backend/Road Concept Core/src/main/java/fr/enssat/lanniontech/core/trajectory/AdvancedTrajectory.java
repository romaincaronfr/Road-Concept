package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;

public class AdvancedTrajectory extends Trajectory {
    private SimpleTrajectory source;
    private SimpleTrajectory destination;
    private ArrayList<PosFunction> Pfs;
    private ArrayList<Double> lengths;
    private ArrayList<Double> Ps;
    private double securityDistance;

    public AdvancedTrajectory(SimpleTrajectory source, SimpleTrajectory destination) {
        this.source = source;
        this.destination = destination;

        securityDistance = 15;

        Pfs = new ArrayList<>();
        lengths = new ArrayList<>();
        Ps = new ArrayList<>();

        Pfs.add(source.getFunction());
        Pfs.add(destination.getFunction());

        double[] p = Pfs.get(0).getInterPos(Pfs.get(1), source.getWidth(), destination.getWidth());

        source.addDestination(this);
        Ps.add(source.getStop());
        Ps.add(p[0]);
        Ps.add(p[1]);
        destination.addSource(this);
        Ps.add(destination.getStop());

        if (source.isInverted()) {
            lengths.add(Ps.get(0) - Ps.get(1));
        } else {
            lengths.add(Ps.get(1) - Ps.get(0));
        }

        if (destination.isInverted()) {
            lengths.add(Ps.get(2) - Ps.get(3));
        } else {
            lengths.add(Ps.get(3) - Ps.get(2));
        }
        length = lengths.get(0) + lengths.get(1);
    }

    public double getSecurityDistance() {
        return securityDistance;
    }

    //Trajectory class implementation

    public Trajectory getNext() {
        return destination;
    }

    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            return destination.getSpeedOfFirst();
        } else {
            return vehiclesSides.get(0).getMyVehicle().getSpeed();
        }
    }

    public double getNextCarSpeed(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            return destination.getSpeedOfFirst();
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    public double getDistanceToFirst() {
        if (vehiclesSides.size() == 0) {
            return length + destination.getDistanceToFirst();
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    public double getDistanceToNext(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            return length - side.getPos() + destination.getDistanceToFirst();
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    public boolean rangeIsFree(double start, double end) {
        int i = 0;
        double pos;
        while (i < vehiclesSides.size()) {
            pos = vehiclesSides.get(i).getPos();
            if (pos >= start && pos <= end) {
                return false;
            } else if (pos > end) {
                return true;
            }
            i++;
        }
        return true;
    }

    public Position getGPS(double pos) {
        if (pos < lengths.get(0)) {
            return source.getGPS(source.getLength() + pos);
        } else {
            return destination.getGPS(pos - length);
        }
    }
}
