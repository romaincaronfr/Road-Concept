package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdvancedTrajectory extends Trajectory {
    private SimpleTrajectory source;
    private SimpleTrajectory destination;
    private List<PosFunction> Pfs;
    private List<Double> lengths;
    private List<Double> Ps;
    private double securityDistance;

    public static Logger LOG = LoggerFactory.getLogger(AdvancedTrajectory.class);

    public AdvancedTrajectory(SimpleTrajectory source, SimpleTrajectory destination,UUID roadId) {
        super(roadId);
        this.source = source;
        this.destination = destination;

        securityDistance = 15;

        Pfs = new ArrayList<>();
        lengths = new ArrayList<>();
        Ps = new ArrayList<>();

        Pfs.add(source.getFunction());
        Pfs.add(destination.getFunction());
        double[] p;
        if (Pfs.get(0).cross(Pfs.get(1))){
             p = Pfs.get(0).getInterPos(Pfs.get(1), source.getWidth(), destination.getWidth());
        }else{
            p = new double[2];
            p[0] = source.getStop();
            p[1] = destination.getStart();
            //todo handle properly the fusion
        }

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

    @Override
    public Trajectory getNext(UUID destination) {
        return getNext();
    }

    public double getSpeedOfFirst() {
        if (vehiclesSides.size() == 0) {
            return destination.getSpeedOfFirst();
        } else {
            return vehiclesSides.get(0).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getNextCarSpeed(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            return destination.getSpeedOfFirst();
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getDistanceToFirst() {
        if (vehiclesSides.size() == 0) {
            return length + destination.getDistanceToFirst();
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    @Override
    public double getDistanceToNext(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            return length - side.getPos() + destination.getDistanceToFirst();
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    @Override
    public Position getGPS(double pos) {
        if (pos < lengths.get(0)) {
            return source.getGPS(source.getLength() + pos);
        } else {
            return destination.getGPS(pos - length);
        }
    }

    public SimpleTrajectory getSource() {
        return source;
    }

    public SimpleTrajectory getDestination() {
        return destination;
    }

    public void explore(Map<Trajectory,Boolean> trajectoryMap){
        if(!trajectoryMap.get(this)){
            trajectoryMap.replace(this,true);
            this.getDestination().explore(trajectoryMap);
        }
    }
}
