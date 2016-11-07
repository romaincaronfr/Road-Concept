package fr.enssat.lanniontech.core.vehicleElements;


import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.trajectory.Trajectory;

public class Side {

    private Trajectory myTrajectory;

    private double pos;
    private Vehicle myVehicle;

    public Side(double pos, Vehicle myVehicle, Lane myLane) {
        this.myVehicle = myVehicle;
        this.pos = pos;
        myTrajectory = myLane.getInsertTrajectory();
        myTrajectory.getIn(this);
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
            myTrajectory = myTrajectory.getNext();
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
}
