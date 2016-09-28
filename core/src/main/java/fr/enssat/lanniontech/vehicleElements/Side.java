package fr.enssat.lanniontech.vehicleElements;

import fr.enssat.lanniontech.roadElements.Lane;

public class Side {
    protected double pos;
    protected Vehicle myVehicle;
    protected Lane myLane;

    Side(double pos, Vehicle myVehicle, Lane myLane){
        this.myVehicle = myVehicle;
        this.pos = pos;
        this.myLane = myLane;
    }

    public double getPos(){
        return pos;
    }

    public Vehicle getMyVehicle() {
        return myVehicle;
    }
}
