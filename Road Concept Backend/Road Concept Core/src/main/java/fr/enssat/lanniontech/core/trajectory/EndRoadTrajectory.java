package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.Map;
import java.util.UUID;

public class EndRoadTrajectory extends Trajectory {
    SimpleTrajectory destination;
    private SimpleTrajectory source;
    private PosFunction pf;

    public EndRoadTrajectory(SimpleTrajectory source, SimpleTrajectory destination,UUID roadId) {
        super(roadId);
        this.source = source;
        this.destination = destination;

        Position A = source.getGPS(source.getStop());
        Position B = destination.getGPS(destination.getStart());
        pf = new PosFunction(A,B);
        length = Position.length(A,B);

        source.addDestination(this);
        destination.addSource(this);


    }

    //------------inherited methods----------//


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

    public Position getGPS(double pos) {
        return pf.get(pos);
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
