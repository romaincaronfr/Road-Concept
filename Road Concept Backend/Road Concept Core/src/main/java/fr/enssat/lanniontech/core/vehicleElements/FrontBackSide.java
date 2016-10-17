package fr.enssat.lanniontech.core.vehicleElements;


import fr.enssat.lanniontech.core.positioning.Trajectory;
import fr.enssat.lanniontech.core.roadElements.Lane;

import java.util.Set;

public class FrontBackSide extends Side {

    private Trajectory myTrajectory;
    private int tSection;

    FrontBackSide(double pos, Vehicle myVehicle, Lane myLane) {
        super(pos, myVehicle, myLane);
        myLane.getIn(this);
        myTrajectory = null;
        tSection = 0;
    }

    public void move(double distance) {
        pos += distance;
        if(myTrajectory==null){
            if(myLane.getTrajectoryMap()==null){
                if (pos > myLane.getLength()) {
                    pos -= myLane.getLength();
                    myLane.getOut(this);
                    myLane = myLane.getNextLane();
                    myLane.getIn(this);
                }
            }else{
                //select the trajectory to follow
                Set<Integer> set = myLane.getTrajectoryMap().keySet();
                myTrajectory = myLane.getTrajectoryMap().get(set.toArray()[0]);
                tSection = 0;
                //todo add trajectory selection
            }
        }else{
            if(myTrajectory.getP(tSection+1)<pos){
                if(tSection != myTrajectory.getSectionSize()){
                    pos =pos - myTrajectory.getP(tSection+1) + myTrajectory.getP(tSection+2);
                    if (myLane != null){
                        myLane.getOut(this);
                        myLane=null;
                    }
                }else{
                    myLane = myTrajectory.getDestination();
                    myLane.getIn(this);
                }
            }
        }
    }

    public double getDistanceToNextCar() {
        return myLane.getDistanceToNext(this);
    }

    public double getNextCarSpeed() {
        return myLane.getNextCarSpeed(this);
    }
}
