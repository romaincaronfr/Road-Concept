package fr.enssat.lanniontech.core.trajectory;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.vehicleElements.Side;

import java.util.ArrayList;

public abstract class Trajectory {

    protected ArrayList<Side> vehiclesSides;
    protected double length;

    public Trajectory(){
        this.vehiclesSides = new ArrayList<>();
    }

    /**
     * remove a vehicle side from the trajectory
     * @param side
     */
    public void getOut(Side side){
        vehiclesSides.remove(side);
    }

    /**
     * insert a vehicle side from the trajectory
     * @param side
     */
    public void getIn(Side side){
        int i = 0;
        while (i < vehiclesSides.size()
                &&
                side.getPos() > vehiclesSides.get(i).getPos()) {
            i++;
        }
        vehiclesSides.add(i, side);
    }

    /**
     * return the position on the trajectory or outside of the current trajectory
     *
     * @param pos
     * @return
     */
    public double getPos(double pos){
        if(pos >= length){
            return pos - length;
        }else {
            return pos;
        }
    }

    /**
     * return the next default trajectory
     */
    public abstract Trajectory getNext();

    /**
     * return the speed of the vehicle ahead
     * @param side
     * @return
     */
    public abstract double getNextCarSpeed(Side side);

    /**
     * return the speed of the first car on the trajectory
     *
     * @return
     */
    public abstract double getSpeedOfFirst();

    /**
     * return the distance of the vehicle ahead
     * @param side
     * @return
     */
    public abstract double getDistanceToNext(Side side);

    /**
     * return the distance to the first vehicle
     *
     * @return
     */
    public abstract double getDistanceToFirst();

    /**
     * return true if the range between start and stop is free
     * @param start
     * @param end
     * @return
     */
    public abstract boolean rangeIsFree(double start, double end);

    /**
     * return the GPS position on the current trajectory
     * @param pos
     * @return
     */
    public abstract Position getGPS(double pos);

    public double getLength() {
        return length;
    }
}
