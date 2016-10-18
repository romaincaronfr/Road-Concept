package fr.enssat.lanniontech.core.vehicleElements;


import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import fr.enssat.lanniontech.core.roadElements.Lane;

public class Side {

    private Trajectory myTrajectory;

    private double pos;
    private Vehicle myVehicle;

    Side(double pos, Vehicle myVehicle, Lane myLane) {
        this.myVehicle = myVehicle;
        this.pos = pos;
        myTrajectory = myLane.getInsertTrajectory();
    }

    public double getPos() {
        return pos;
    }

    public Vehicle getMyVehicle() {
        return myVehicle;
    }

    public void move(double distance) {
        double pos = myTrajectory.getPos(this.pos+distance);
        if(this.pos > pos){
            myTrajectory = myTrajectory.getNext();
        }
        this.pos = pos;
    }

    public double getDistanceToNextCar() {
        return myTrajectory.getDistanceToNext(this);
    }

    public double getNextCarSpeed() {
        return myTrajectory.getNextCarSpeed(this);
    }

    public Position getGPS(){
        return myTrajectory.getGPS(pos);
    }
}
