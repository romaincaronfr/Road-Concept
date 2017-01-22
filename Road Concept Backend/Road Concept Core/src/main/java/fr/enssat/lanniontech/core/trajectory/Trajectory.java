package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Congestion;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Trajectory {

    protected List<Side> vehiclesSides;
    private double speed;
    private Position position;
    protected double length;
    protected UUID roadId;

    public Trajectory(UUID roadId, Position position, double speed) {
        this.roadId = roadId;
        this.vehiclesSides = new ArrayList<>();
        this.position = position;
        this.speed = speed;
    }

    public Position getPosition() {
        return position;
    }

    /**
     * remove a vehicle side from the trajectory
     */
    public void getOut(Side side) {
        vehiclesSides.remove(side);
    }

    /**
     * insert a vehicle side from the trajectory
     */
    public void getIn(Side side) {
        int i = 0;
        while (i < vehiclesSides.size() && side.getPos() > vehiclesSides.get(i).getPos()) {
            i++;
        }
        vehiclesSides.add(i, side);
    }

    /**
     * return the position on the trajectory or outside of the current trajectory
     */
    public double getPos(double pos) {
        if (pos >= length) {
            return pos - length;
        } else {
            return pos;
        }
    }

    /**
     * return true if the range between start and stop is free
     */
    public boolean rangeIsFree(double pos, double before, double behind) {
        boolean res = true;
        double dist = getNext().getDestinationPos() - (pos + before);
        if (dist > 0) {
            res &= true;
        } else {
            res &= getNext().getDestination().getDistanceToFirst(dist) > dist;
        }

        dist = getPrev().getDestinationPos() + pos - behind;
        if (dist > 0) {
            res &= true;
        } else {
            res &= getPrev().getSource().getDistanceToLast(dist) > dist;
        }

        for (Side s : vehiclesSides) {
            if (s.getPos() > pos - behind && s.getPos() < pos + before) {
                res &= false;
            }
        }
        return res;
    }

    protected abstract double getDistanceToLast(double freeDistance);

    public Congestion getCongestion() {
        double notFreeSpace = 0;
        for (Side s : vehiclesSides) {
            if (s.getLength() > 0){
                notFreeSpace += s.getLength();
            }
        }

        return new Congestion(notFreeSpace,length);
    }

    /**
     * return the id of the trajectory road
     */
    public UUID getRoadId() {
        return roadId;
    }

    /**
     * return the length of the trajectory
     */
    public double getLength() {
        return length;
    }

    /**
     * return all the destination trajectories
     */
    public abstract List<TrajectoryJunction> getAllNext();

    /**
     * return the next default trajectory
     */
    public abstract TrajectoryJunction getNext();


    /**
     * return previous trajectory
     */
    public abstract TrajectoryJunction getPrev();

    /**
     * return the next trajectory to the road
     */
    public abstract TrajectoryJunction getNext(UUID destination);

    public abstract List<TrajectoryInformation> getInformations(Side side,double distanceOut);

    public abstract List<TrajectoryInformation> getInformations(TrajectoryInformation I,double pos);

    /**
     * return the speed of the vehicle ahead
     */
    @Deprecated
    public abstract double getNextCarSpeed(Side side);

    /**
     * return the speed of the first car on the trajectory
     */
    @Deprecated
    public abstract double getSpeedOfFirst();

    /**
     * return the distance of the vehicle ahead
     */
    @Deprecated
    public abstract double getDistanceToNext(Side side, double freeDistance);

    /**
     * return the distance to the first vehicle
     */
    @Deprecated
    public abstract double getDistanceToFirst(double freeDistance);

    /**
     * return the GPS position on the current trajectory
     */
    public abstract Position getGPS(double pos);

    /**
     * explore the trajectory and set its value in Map at true, used to verify the integrity of the model
     */
    public abstract void explore(Map<Trajectory, Boolean> trajectoryMap);

    /**
     * return the next intersection
     */
    public abstract Intersection getNextIntersection();

    public double getSpeed() {
        return speed;
    }
}
