package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.informations.InformationType;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformation;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformator;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        informator.setExplored(this);
        double distance;

        int pos = vehiclesSides.indexOf(side);
        if (pos != vehiclesSides.size() - 1) {
            distance = Math.abs(vehiclesSides.get(pos + 1).getPos() - side.getPos());
            informator.addInformation(new TrajectoryInformation(distance, vehiclesSides.get(pos + 1).getMyVehicle().getSpeed(), InformationType.VEHICLE));
        }
        distance = Math.abs(side.getPos() - destination.getSourcePos());
        if (distance < distanceOut) {
            informator.addJunction(distance, destination);
        } else {
            informator.addInformation(new TrajectoryInformation(distance, 0, InformationType.FREE));
        }

        informator.computeInformations();

        return informator;
    }

    @Override
    public void getInformations(double pos, double distance, TrajectoryInformator informator) {
        double tempDistance;
        informator.setExplored(this);
        if (!vehiclesSides.isEmpty()) {
            Side nearestSide = vehiclesSides.get(0);
            double nearestDistance = Math.abs(pos - nearestSide.getPos());
            for (int i = 1; i < vehiclesSides.size(); i++) {
                tempDistance = Math.abs(pos - vehiclesSides.get(i).getPos());
                if (tempDistance < nearestDistance) {
                    nearestDistance = distance;
                    nearestSide = vehiclesSides.get(i);
                }
            }

            if (nearestSide.getLength() < 0) {
                informator.addInformation(new TrajectoryInformation(distance + nearestDistance, -nearestSide.getMyVehicle().getSpeed(), InformationType.INCOMING_VEHICLE));
            } else {
                informator.addInformation(new TrajectoryInformation(distance + nearestDistance, nearestSide.getMyVehicle().getSpeed(), InformationType.VEHICLE));
            }
        }

        tempDistance = Math.abs(source.getDestinationPos() - pos);
        if (tempDistance != 0) {
            tempDistance += distance;
            if (tempDistance > informator.getDistanceOut()) {
                informator.addInformation(new TrajectoryInformation(tempDistance, 0, InformationType.FREE));
            } else {
                informator.addJunction(tempDistance, source);
            }
        }

        tempDistance = Math.abs(destination.getSourcePos() - pos);
        if (tempDistance != 0) {
            tempDistance += distance;
            if (tempDistance > informator.getDistanceOut()) {
                informator.addInformation(new TrajectoryInformation(tempDistance, 0, InformationType.FREE));
            } else {
                informator.addJunction(tempDistance, destination);
            }
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
