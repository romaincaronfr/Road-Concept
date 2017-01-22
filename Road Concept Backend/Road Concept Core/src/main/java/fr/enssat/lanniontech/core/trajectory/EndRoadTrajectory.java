package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.PosFunction;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
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
    public List<TrajectoryInformation> getInformations(Side side,double distanceOut) {
        List<TrajectoryInformation> trajectoryInformations = new ArrayList<>();
        int pos = vehiclesSides.indexOf(side);
        if (pos == vehiclesSides.size() - 1) {
            //if we are the last car
            TrajectoryInformation I = new TrajectoryInformation(distanceOut);
            I.addToExplored(this);

            if(I.addToDistance(destination.getDestinationPos()-side.getPos())){
                trajectoryInformations.addAll(destination.getDestination().
                        getInformations(I,destination.getSourcePos()));
            }else{
                trajectoryInformations.add(I);
            }
        }else {
            TrajectoryInformation I = new TrajectoryInformation(distanceOut);
            I.addToDistance(vehiclesSides.get(pos + 1).getPos() - side.getPos());
            I.setSpeed(vehiclesSides.get(pos + 1).getMyVehicle().getSpeed());
            trajectoryInformations.add(I);
        }

        return trajectoryInformations;
    }

    @Override
    public List<TrajectoryInformation> getInformations(TrajectoryInformation I, double pos) {
        List<TrajectoryInformation> trajectoryInformations = new ArrayList<>();
        TrajectoryInformation TI;
        I.addToExplored(this);
        if(vehiclesSides.isEmpty()){
            if(!I.isExplored(source.getSource())){
                TI = I.clone();
                if(TI.addToDistance(source.getSourcePos()-pos)){
                    trajectoryInformations.addAll(source.getSource().getInformations(TI,source.getSourcePos()));
                }else {
                    trajectoryInformations.add(I);
                }
            }

            if(!I.isExplored(destination.getDestination())){
                TI = I.clone();
                if(TI.addToDistance(destination.getDestinationPos()-pos)){
                    trajectoryInformations.addAll(destination.getDestination().getInformations(TI,destination.getDestinationPos()));
                }else {
                    trajectoryInformations.add(I);
                }
            }
        }else{
            Side nearestSide = vehiclesSides.get(0);
            for (int i = 1; i < vehiclesSides.size(); i++) {
                if(Math.abs(nearestSide.getPos()-pos) >
                        Math.abs(vehiclesSides.get(i).getPos()-pos)){
                    nearestSide = vehiclesSides.get(i);
                }
            }
            I.addToDistance(Math.abs(nearestSide.getPos()-pos));
            double speed = nearestSide.getMyVehicle().getSpeed();
            if(nearestSide.getLength()>0){
                I.setSpeed(speed);
            }else {
                I.setSpeed(-speed);
            }

        }

        return trajectoryInformations;
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
