package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.informations.InformationType;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformation;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformator;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.*;

public class EndRoadTrajectory extends Trajectory {
    private TrajectoryJunction destination;
    private TrajectoryJunction source;
    private PosFunction pf;

    public EndRoadTrajectory(SimpleTrajectory source, SimpleTrajectory destination, UUID roadId) {
        super(roadId, Position.getMean(source.getGPS(source.getStop()), destination.getGPS(0)), source.getSpeed());

        Position A = source.getGPS(source.getStop());
        Position B = destination.getGPS(0);
        pf = new PosFunction(A, B);
        length = Position.length(A, B);
    }

    //------------inherited methods----------//


    @Override
    public TrajectoryJunction getNext() {
        return destination;
    }

    @Override
    public TrajectoryJunction getNext(UUID destination) {
        return getNext();
    }

    @Override
    public TrajectoryInformator getInformations(Side side, double distanceOut) {
        TrajectoryInformator informator = new TrajectoryInformator(distanceOut);
        double distance;

        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            distance = Math.abs(vehiclesSides.get(pos + 1).getPos() - side.getPos());
            if(distance < distanceOut){
                informator.addInformation(
                        new TrajectoryInformation(
                                distance,
                                vehiclesSides.get(pos + 1).getMyVehicle().getSpeed(),
                                InformationType.VEHICLE));
            }else {
                informator.addInformation(
                        new TrajectoryInformation(
                                distance,
                                0,
                                InformationType.FREE));
            }
        } else {
            distance = Math.abs(side.getPos() - destination.getSourcePos());
            if(distance < distanceOut){
                informator.addJunction(distance,destination);
            }else{
                informator.addInformation(
                        new TrajectoryInformation(
                                distance,
                                0,
                                InformationType.FREE));
            }
        }
        informator.setExplored(this);
        informator.computeInformations();

        return informator;
    }

    @Override
    public void getInformations(double pos, double distance, TrajectoryInformator informator) {
        Map<Double, TrajectoryJunction> trajectoryJunctionMap = new HashMap<>();
    }

    @Override
    public double getSpeedOfFirst() {
        if (vehiclesSides.isEmpty()) {
            return destination.getDestination().getSpeedOfFirst();
        } else {
            return vehiclesSides.get(0).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getNextCarSpeed(Side side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            return destination.getDestination().getSpeedOfFirst();
        } else {
            return vehiclesSides.get(pos + 1).getMyVehicle().getSpeed();
        }
    }

    @Override
    public double getDistanceToFirst(double freeDistance) {
        if (vehiclesSides.isEmpty()) {
            if (freeDistance - length > 0) {
                return length + destination.getDestination().getDistanceToFirst(freeDistance - length);
            } else {
                return length;
            }
        } else {
            return vehiclesSides.get(0).getPos();
        }
    }

    @Override
    protected double getDistanceToLast(double freeDistance) {
        if (vehiclesSides.isEmpty()) {
            if (freeDistance - length > 0) {
                return length + source.getSource().getDistanceToLast(freeDistance - length);
            } else {
                return length;
            }
        } else {
            return length - vehiclesSides.get(vehiclesSides.size() - 1).getPos();
        }
    }

    @Override
    public double getDistanceToNext(Side side, double freeDistance) {
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            double distance = length - side.getPos();
            if (freeDistance - distance > 0) {
                return length - side.getPos() + destination.getDestination().getDistanceToFirst(freeDistance - distance);
            } else {
                return distance;
            }
        } else {
            return vehiclesSides.get(pos + 1).getPos() - side.getPos();
        }
    }

    @Override
    public Position getGPS(double pos) {
        return pf.get(pos);
    }

    public TrajectoryJunction getSource() {
        return source;
    }

    public TrajectoryJunction getDestination() {
        return destination;
    }

    public void setDestination(TrajectoryJunction destination) {
        this.destination = destination;
    }

    public void setSource(TrajectoryJunction source) {
        this.source = source;
    }

    @Override
    public void explore(Map<Trajectory, Boolean> trajectoryMap) {
        if (!trajectoryMap.get(this)) {
            trajectoryMap.replace(this, true);
            destination.getDestination().explore(trajectoryMap);
        }
    }

    @Override
    public Intersection getNextIntersection() {
        return destination.getDestination().getNextIntersection();
    }


    @Override
    public List<TrajectoryJunction> getAllNext() {
        List<TrajectoryJunction> l = new ArrayList<>();
        l.add(destination);
        return l;
    }

    public TrajectoryJunction getPrev() {
        return source;
    }
}
