package fr.enssat.lanniontech.core.vehicleElements;


import fr.enssat.lanniontech.core.roadElements.Lane;

public class FrontBackSide extends Side {

    FrontBackSide(double pos, Vehicle myVehicle, Lane myLane){
        super(pos,myVehicle,myLane);
        myLane.getIn(this);
    }

    public void move(double distance){
        pos += distance;
        if(pos > myLane.getLength()){
            pos -= myLane.getLength();
            myLane.getOut(this);
            myLane = myLane.getNextLane();
            myLane.getIn(this);
        }
    }


    public double getDistanceToNextCar() {
        return myLane.getDistanceToNext(this);
    }

    public double getNextCarSpeed() {
        return myLane.getNextCarSpeed(this);
    }
}
