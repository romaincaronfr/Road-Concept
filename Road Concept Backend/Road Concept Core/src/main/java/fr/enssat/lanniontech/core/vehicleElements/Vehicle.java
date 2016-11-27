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
    private VehicleType type;
    private VehicleAI AI;

    /**
     * constructor of a vehicle, place the newly created vehicle on the desired lane
     *
     * @param ID             identifier for the vehicle
     * @param start          lane where the vehicle is placed
     * @param startPos       position in the lane of the new vehicle
     * @param length         length of the vehicle
     * @param historyManager
     */
    private Vehicle(int ID, Lane start, double startPos, double length,
                    HistoryManager historyManager, Path myPath, VehicleAI AI) {
        this.ID = ID;
        this.length = length;
        this.distanceDone = 0;
        this.myPath = myPath;
        this.frontSide = new Side(length + startPos, this, start);
        this.backSide = new Side(startPos, this, start);
        this.historyManager = historyManager;
        this.AI = AI;
    }

    /**
     * this method will actualise the acceleration of the vehicle accordingly to it's environment and parameters
     */
    public void updateAcceleration() {
        double nextCarDist = frontSide.getDistanceToNextCar(AI.getFreeDistance());
        double nextCarSpeed = 0;
        if(nextCarDist < AI.getFreeDistance()){
            nextCarSpeed = frontSide.getNextCarSpeed();
        }
        AI.updateAcceleration(nextCarDist,nextCarSpeed,roadMaxSpeed());
    }

    private double roadMaxSpeed(){
        return Tools.kphToMph(90);
        //todo get the max speed of the current road
    }

    /**
     * actualize the position with the speed of the vehicle, then actualize it's speed for the next cycle
     */
    public void updatePos(double time) {
        double dDone = AI.getDistanceDone(time);
        this.distanceDone += dDone;
        if (Double.isNaN(dDone)) {
            System.err.println("overflow");
        }
        backSide.moveOnPath(dDone);
        frontSide.moveOnPath(dDone);
    }

    public void logPosition(int time){
        historyManager.AddPosition(getGPSPosition(time));
    }

    public double getSpeed() {
        return AI.getSpeed();
    }

    public SpaceTimePosition getGPSPosition(int time) {
        return SpaceTimePosition.getMean(frontSide.getGPS(), backSide.getGPS(), time, ID,type);
    }

    public UUID getPathStep(int i) {
        return myPath.getStep(i);
    }

    public boolean isArrived() {
        return backSide.getNextRoad() == null;
    }

    public static Vehicle createCar(int ID, Lane start, double startPos, HistoryManager historyManager, Path myPath){
        double length = 3.5;
        VehicleAI AI = new VehicleAI(3,1.5,4);
        Vehicle V = new Vehicle(ID,start,startPos,length,historyManager,myPath,AI);
        V.type = VehicleType.CAR;
        return V;
    }

    public static Vehicle createTruck(int ID, Lane start, double startPos, HistoryManager historyManager, Path myPath){
        double length = 15;
        VehicleAI AI = new VehicleAI(2,1,10);
        Vehicle V = new Vehicle(ID,start,startPos,length,historyManager,myPath,AI);
        V.type = VehicleType.TRUCK;
        return V;
    }
}
