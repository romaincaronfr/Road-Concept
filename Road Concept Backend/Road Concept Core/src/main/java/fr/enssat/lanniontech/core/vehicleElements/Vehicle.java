package fr.enssat.lanniontech.core.vehicleElements;

import fr.enssat.lanniontech.core.Tools;
import fr.enssat.lanniontech.core.managers.HistoryManager;
import fr.enssat.lanniontech.core.pathFinding.Path;
import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;
import fr.enssat.lanniontech.core.roadElements.Lane;

import java.util.UUID;

public class Vehicle {
    private final double length;
    private double distanceDone;// in m
    private Side frontSide;
    private Side backSide;
    private int ID;
    private HistoryManager historyManager;
    private Path myPath;

    private double Va = Tools.kphToMph(90);      //speed in m/s
    private double A;           //acceleration
    private double a;           //max acceleration
    private double b;           //deceleration
    private double T;           //gap time between two car
    private double lambda = 4;  //function coefficient
    private double v0;          //desired speed
    private double s0 = 2;      //minimum distance between two cars

    /**
     * constructor of a vehicle, place the newly created vehicle on the desired lane
     *
     * @param ID             identifier for the vehicle
     * @param start          lane where the vehicle is placed
     * @param startPos       position in the lane of the new vehicle
     * @param length         length of the vehicle
     * @param historyManager
     */
    private Vehicle(int ID, Lane start, double startPos, double length, HistoryManager historyManager, Path myPath) {
        this.ID = ID;
        this.length = length;
        this.distanceDone = 0;
        this.myPath = myPath;
        this.frontSide = new Side(length + startPos, this, start);
        this.backSide = new Side(startPos, this, start);
        this.historyManager = historyManager;
    }

    /**
     * this method will actualise the acceleration of the vehicle accordingly to it's environment and parameters
     */
    public void updateAcceleration() {
        //double Sa = this.distanceToNextCar();
        //double Sprime = s0 + Va * T + (Va * (Va - nextCarSpeed())) / (2 * Math.sqrt(a * b));
        //A = a * (1 - Math.pow(Va / v0, lambda) - Math.pow(Sprime / Sa, 2));
        A = 0;
        //TODO reactivate
    }

    /**
     * this method will return the distance to the next car
     */
    private double distanceToNextCar() {
        return frontSide.getDistanceToNextCar();
    }

    /**
     * this method will return the speed of the car toward
     */
    private double nextCarSpeed() {
        return frontSide.getNextCarSpeed();
    }

    /**
     * actualize the position with the speed of the vehicle, then actualize it's speed for the next cycle
     */
    public void updatePos(double time) {
        double dDone = Va * time;
        this.distanceDone += dDone;
        if (Double.isNaN(dDone)) {
            System.err.println("overflow");
        }
        backSide.moveOnPath(dDone);
        frontSide.moveOnPath(dDone);
        Va += A * time;
    }

    public void logPosition(int time){
        historyManager.AddPosition(getGPSPosition(time));
    }

    public double getSpeed() {
        return Va;
    }

    public SpaceTimePosition getGPSPosition(int time) {
        return SpaceTimePosition.getMean(frontSide.getGPS(), backSide.getGPS(), time, ID);
    }

    public UUID getPathStep(int i) {
        return myPath.getStep(i);
    }

    public boolean isArrived() {
        return backSide.getNextRoad() == null;
    }

    public static Vehicle createCar(int ID, Lane start, double startPos, HistoryManager historyManager, Path myPath){
        double length = 3.5;
        Vehicle V = new Vehicle(ID,start,startPos,length,historyManager,myPath);
        V.a = 3;
        V.b = 1.5;
        V.T = 4;
        return V;
    }

    public static Vehicle createTruck(int ID, Lane start, double startPos, HistoryManager historyManager, Path myPath){
        double length = 15;
        Vehicle V = new Vehicle(ID,start,startPos,length,historyManager,myPath);
        V.a = 2;
        V.b = 1;
        V.T = 10;
        return V;
    }
}
