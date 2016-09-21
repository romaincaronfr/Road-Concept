package fr.enssat.lanniontech;

public class Side {
    protected double pos;
    protected Vehicle myVehicle;
    protected Lane myLane;

    Side(double pos, Vehicle myVehicle, Lane myLane){
        this.myVehicle = myVehicle;
        this.pos = pos;
        this.myLane = myLane;
    }
}
