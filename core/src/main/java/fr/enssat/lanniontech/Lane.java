package fr.enssat.lanniontech;

import java.util.ArrayList;

public class Lane {
    private Road myRoad;
    private Lane nextLane;
    private ArrayList<FrontBackSide> vehiclesSides;
    private double length;
    private double width;

    Lane(Road myRoad,double length){
        this.myRoad = myRoad;
        this.length = length;
        nextLane = this;
        vehiclesSides = new ArrayList<FrontBackSide>();
        width = 3.5;
    }

    public double getLength() {
        return length;
    }

    public void getOut(FrontBackSide side) {
        vehiclesSides.remove(side);
    }

    public void getIn(FrontBackSide side) {
        int i = 0;
        System.out.println(vehiclesSides.size());
        while(i<vehiclesSides.size()&&side.pos>vehiclesSides.get(i).pos)
        {
            i++;
        }
        vehiclesSides.add(i,side);
    }

    public Lane getNextLane(){
        return nextLane;
    }

    public double getDistanceToNext(FrontBackSide side){
        int pos = vehiclesSides.indexOf(side);
        if (pos==vehiclesSides.size()-1) {
            return length - side.pos + nextLane.getDistanceToFirst();
        }else {
            return vehiclesSides.get(pos + 1).pos - side.pos;
        }
    }

    public double getDistanceToFirst(){
        if (vehiclesSides.size()==0){
            return length;
        }else{
            return vehiclesSides.get(0).pos;
        }
    }

    public double getNextCarSpeed(FrontBackSide side) {
        int pos = vehiclesSides.indexOf(side);
        if (pos==vehiclesSides.size()-1) {
            return nextLane.getSpeedOfFirst();
        }else {
            return vehiclesSides.get(pos + 1).myVehicle.getSpeed();
        }
    }

    public double getSpeedOfFirst(){
        if (vehiclesSides.size()==0){
            return length;
        }else{
            return vehiclesSides.get(0).myVehicle.getSpeed();
        }
    }

    public Position getPosition(double pos){
        return myRoad.getPosition(this,pos,width/2);
    }
}
