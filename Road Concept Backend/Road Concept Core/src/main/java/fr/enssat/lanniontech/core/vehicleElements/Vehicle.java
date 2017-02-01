package fr.enssat.lanniontech.core.vehicleElements;

import fr.enssat.lanniontech.core.Tools;
import fr.enssat.lanniontech.core.managers.HistoryManager;
import fr.enssat.lanniontech.core.pathFinding.Path;
import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformation;
import fr.enssat.lanniontech.core.trajectory.informations.TrajectoryInformator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Vehicle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Vehicle.class);

    private final double length;
    private double distanceDone;// in m
    private Side frontSide;
    private Side backSide;
    private int ID;
    private HistoryManager historyManager;
    private Path myPath;
    private VehicleType type;
    private VehicleAI AI;
    private double timeAlive;

    /**
     * constructor of a vehicle, place the newly created vehicle on the desired lane
     *
     * @param ID
     *         identifier for the vehicle
     * @param start
     *         lane where the vehicle is placed
     * @param startPos
     *         position in the lane of the new vehicle
     * @param length
     *         length of the vehicle
     */
    private Vehicle(int ID, Lane start, double startPos, double length, HistoryManager historyManager, Path myPath, VehicleAI AI) {
        this.setID(ID);
        this.length = length;
        distanceDone = 0;
        this.setMyPath(myPath);
        this.setFrontSide(new Side(length + startPos, this, start, -length));
        this.setBackSide(new Side(startPos, this, start, length));
        this.setHistoryManager(historyManager);
        this.setAI(AI);
        timeAlive = 0;
    }

    /**
     * this method will actualise the acceleration of the vehicle accordingly to it's environment and parameters
     */
    public void updateAcceleration() {
        double freeDistance = AI.getFreeDistance(roadMaxSpeed());
        TrajectoryInformator informator = frontSide.getInformations(freeDistance);
        TrajectoryInformation information = informator.getNearest();

        double nextCarSpeed = AI.getSpeed();
        if (!information.isFree()) {
            nextCarSpeed = information.getSpeed();
        }
        getAI().updateAcceleration(information.getDistance(), nextCarSpeed, roadMaxSpeed());
    }

    private double roadMaxSpeed() {
        double maxSpeed = frontSide.getMaxSpeed();
        if (type == VehicleType.TRUCK) {
            maxSpeed = maxSpeed - 5;
        }
        return Tools.kphToMph(maxSpeed);
    }

    /**
     * actualize the position with the speed of the vehicle, then actualize it's speed for the next cycle
     */
    public void updatePos(double time) {
        timeAlive += time;
        double dDone = getAI().getDistanceDone(time);
        this.setDistanceDone(getDistanceDone() + dDone);
        getBackSide().moveOnPath(dDone);
        getFrontSide().moveOnPath(dDone);
    }

    public void logPosition(int time) {
        getHistoryManager().addPosition(getGPSPosition(time));
    }

    public double getSpeed() {
        return getAI().getSpeed();
    }

    public SpaceTimePosition getGPSPosition(int time) {
        return SpaceTimePosition.getMean(getFrontSide().getGPS(), getBackSide().getGPS(), time, getID(), getType());
    }

    public UUID getPathStep(int i) {
        return getMyPath().getStep(i);
    }

    public boolean isArrived() {
        return getBackSide().getNextRoad() == null;
    }

    public void removeVehicle() {
        frontSide.removeFromRoad();
        backSide.removeFromRoad();
    }

    public static Vehicle createCar(int ID, Lane start, double startPos, HistoryManager historyManager, Path myPath) {
        double length = 3.5;
        VehicleAI AI = new VehicleAI(1, 4, 2);
        Vehicle V = new Vehicle(ID, start, startPos, length, historyManager, myPath, AI);
        V.setType(VehicleType.CAR);
        return V;
    }

    public static Vehicle createTruck(int ID, Lane start, double startPos, HistoryManager historyManager, Path myPath) {
        double length = 5;
        VehicleAI AI = new VehicleAI(0.5, 2, 5);
        Vehicle V = new Vehicle(ID, start, startPos, length, historyManager, myPath, AI);
        V.setType(VehicleType.TRUCK);
        return V;
    }

    public double getLength() {
        return length;
    }

    public double getDistanceDone() {
        return distanceDone;
    }

    public void setDistanceDone(double distanceDone) {
        this.distanceDone = distanceDone;
    }

    public Side getFrontSide() {
        return frontSide;
    }

    public void setFrontSide(Side frontSide) {
        this.frontSide = frontSide;
    }

    public Side getBackSide() {
        return backSide;
    }

    public void setBackSide(Side backSide) {
        this.backSide = backSide;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public Path getMyPath() {
        return myPath;
    }

    public void setMyPath(Path myPath) {
        this.myPath = myPath;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public VehicleAI getAI() {
        return AI;
    }

    public void setAI(VehicleAI AI) {
        this.AI = AI;
    }

    public VehicleStats getStats() {
        double AvgSpeed = Tools.mpsToKph(distanceDone / timeAlive);
        VehicleStats stats = new VehicleStats(this.ID, (int) AvgSpeed, (int) timeAlive, (int) distanceDone);
        return stats;
    }

    public Trajectory getTrajectory() {
        if (backSide.getMyTrajectory() == frontSide.getMyTrajectory()) {
            return frontSide.getMyTrajectory();
        } else {
            return null;
        }
    }

    public void updatePath(Path p) {
        myPath = p;
        frontSide.resetPath();
        backSide.resetPath();
    }
}
