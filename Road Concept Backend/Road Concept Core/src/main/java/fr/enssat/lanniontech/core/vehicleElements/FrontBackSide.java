package fr.enssat.lanniontech.core.vehicleElements;


import fr.enssat.lanniontech.core.positioning.Trajectory;
import fr.enssat.lanniontech.core.roadElements.Lane;

public class FrontBackSide extends Side {

    Trajectory myTrajectory;

    FrontBackSide(double pos, Vehicle myVehicle, Lane myLane) {
        super(pos, myVehicle, myLane);
        myLane.getIn(this);
        myTrajectory = null;
    }

    public void move(double distance) {
        pos += distance;
        if(myTrajectory!=null){
            if(myLane.getTrajectoryMap()==null){
                if (pos > myLane.getLength()) {
                    pos -= myLane.getLength();
                    myLane.getOut(this);
                    myLane = myLane.getNextLane();
                    myLane.getIn(this);
                }
            }
        }else{
            //todo add trajectory selection
        }
    }


    public double getDistanceToNextCar() {
        return myLane.getDistanceToNext(this);
    }

    public double getNextCarSpeed() {
        return myLane.getNextCarSpeed(this);
    }
}
