package fr.enssat.lanniontech.core.vehicleElements;


import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Side {

    public static Logger LOG = LoggerFactory.getLogger(Side.class);

    private Trajectory myTrajectory;

    private double pos;
    private Vehicle myVehicle;
    private UUID myRoad;
    private UUID nextRoad;
    private int pathStep;

    public Side(double pos, Vehicle myVehicle, Lane myLane) {
        this.myVehicle = myVehicle;
        this.pos = pos;
        myTrajectory = myLane.getInsertTrajectory();
        myTrajectory.getIn(this);
        pathStep = 1;
        myRoad = null;
        nextRoad = null;
        if(myVehicle!=null){
            myRoad = myVehicle.getPathStep(0);
            nextRoad = myVehicle.getPathStep(pathStep);
        }
    }

    public double getPos() {
        return pos;
    }

    public Vehicle getMyVehicle() {
        return myVehicle;
    }

    public void move(double distance) {
        double pos = myTrajectory.getPos(this.pos + distance);
        if (this.pos > pos) {
            myTrajectory.getOut(this);
            myTrajectory = myTrajectory.getNext().getDestination();
            try{
                myTrajectory.getIn(this);
            } catch ( NullPointerException e ){
                System.err.println(myTrajectory);
                throw e;
            }

        }
        this.pos = pos;
    }

    public void moveOnPath(double distance) {
        double pos = myTrajectory.getPos(this.pos + distance);
        if (this.pos > pos) {
            myTrajectory.getOut(this);
            myTrajectory = myTrajectory.getNext(nextRoad).getDestination();
            if (myTrajectory.getRoadId()!=myRoad){
                myRoad = myTrajectory.getRoadId();
                if(nextRoad != myRoad){
                    LOG.error(nextRoad + " != " + myRoad);
                }
                nextRoad = myVehicle.getPathStep(++pathStep);
                LOG.debug("trajectory changed, now on road : "+myRoad);
                LOG.debug("is on step : "+pathStep);
            }
            try{
                myTrajectory.getIn(this);
            } catch ( NullPointerException e ){
                System.err.println(myTrajectory);
                throw e;
            }
        }
        this.pos = pos;
    }

    public double getDistanceToNextCar() {
        return myTrajectory.getDistanceToNext(this);
    }

    public double getNextCarSpeed() {
        return myTrajectory.getNextCarSpeed(this);
    }

    public Position getGPS() {
        return myTrajectory.getGPS(pos);
    }

    public UUID getNextRoad() {
        return nextRoad;
    }
}
