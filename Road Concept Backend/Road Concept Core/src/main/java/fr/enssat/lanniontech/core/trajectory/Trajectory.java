package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class Trajectory {

    protected List<Side> vehiclesSides;
    protected double length;
    protected double notFreeSpace;
    private List<Integer> loggedVehicles;
    protected UUID roadId;

    public Trajectory(UUID roadId) {
        loggedVehicles = new ArrayList<>();
        notFreeSpace = 0;
        this.roadId = roadId;
        this.vehiclesSides = new ArrayList<>();
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

    public void addCar(double space, int carId) {
        if (!loggedVehicles.contains(carId)) {
            notFreeSpace += space;
        }
    }

    public double getFreeSpaceRatio() {
        double res = notFreeSpace / length;
        notFreeSpace = 0;
        loggedVehicles.clear();
        return res;
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
     * return the next default trajectory
     */
    public abstract TrajectoryJunction getNext();

    /**
     * return the next trajectory to the road
     */
    public abstract TrajectoryJunction getNext(UUID destination);

    /**
     * return the speed of the vehicle ahead
     */
    public abstract double getNextCarSpeed(Side side);

    /**
     * return the speed of the first car on the trajectory
     */
    public abstract double getSpeedOfFirst();

    /**
     * return the distance of the vehicle ahead
     */
    public abstract double getDistanceToNext(Side side, double freeDistance);

    /**
     * return the distance to the first vehicle
     */
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
     * return the position of the next intersection
     */
    public abstract Intersection getNextIntersection();

}
