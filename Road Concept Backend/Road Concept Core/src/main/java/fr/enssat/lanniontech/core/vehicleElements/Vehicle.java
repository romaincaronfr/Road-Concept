package fr.enssat.lanniontech.core.vehicleElements;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;

import java.util.ArrayList;

public class Vehicle {
    private final double length;
    private double distanceDone;// in m
    private FrontBackSide frontSide;
    private FrontBackSide backSide;
    private int ID;

    private double Va = 0;      //speed in m/s
    private double A;           //acceleration
    private double a = 2;       //max acceleration
    private double b = 1;       //deceleration
    private double T = 1;       //gap time between two car
    private double lambda = 4;
    private double v0;          //desired speed
    private double s0 = 2;      //minimum distance between two cars
    private long time;
    private ArrayList<Position> positionHistory;


    /**
     * constructor of a vehicle, place the newly created vehicle on the desired lane
     *
     * @param ID       identifier for the vehicle
     * @param start    lane where the vehicle is placed
     * @param startPos position in the lane of the new vehicle
     * @param length   length of the vehicle
     * @param speed    maximum speed of the vehicle
     */
    public Vehicle(int ID, Lane start, double startPos, double length, double speed, long initialTime) {
        this.ID = ID;
        this.length = length;
        this.distanceDone = 0;
        this.v0 = speed;
        this.frontSide = new FrontBackSide(length + startPos, this, start);
        this.backSide = new FrontBackSide(startPos, this, start);
        this.time = initialTime;
        positionHistory = new ArrayList<Position>();
        positionHistory.add(getGPSPosition());
    }

    public void log() {
        System.out.println("ID: " + this.ID);
        System.out.println("distance: " + distanceDone);
        System.out.println("speed: " + Va);
        //System.out.println("backSide pos: "+backSide.pos);
        //System.out.println("frontSide pos: "+frontSide.pos);
        System.out.println("Acceleration: " + A);
        System.out.println("distance to next car: " + distanceToNextCar());
        System.out.println("Position: " + getGPSPosition());
        System.out.println("-----------------------------------------");
    }

    /**
     * this method will actualise the acceleration of the vehicle accordingly to it's environment and parameters
     */
    public void updateAcceleration() {
        double Sa = this.distanceToNextCar();
        double Sprime = s0 + Va * T + (Va * (Va - nextCarSpeed())) / (2 * Math.sqrt(a * b));
        A = a * (1 - Math.pow(Va / v0, lambda) - Math.pow(Sprime / Sa, 2));
    }

    /**
     * this method will return the distance to the next car
     *
     * @return
     */
    private double distanceToNextCar() {
        return frontSide.getDistanceToNextCar();
    }

    /**
     * this method will return the speed of the car toward
     *
     * @return
     */
    private double nextCarSpeed() {
        return frontSide.getNextCarSpeed();
    }

    /**
     * actualize the position with the speed of the vehicle, then actualize it's speed for the next cycle
     *
     * @param time
     */
    public void updatePos(double time, boolean log) {
        double dDone = Va * time;
        this.distanceDone += dDone;
        backSide.move(dDone);
        frontSide.move(dDone);
        time++;
        if (log) {
            positionHistory.add(getGPSPosition());
        }
        Va += A * time;
    }

    public double getSpeed() {
        return Va;
    }

    public Position getGPSPosition() {
        double pos = frontSide.getPos() - length / 2;
        Position posGPS;
        if (frontSide.getPos() >= 0) {
            posGPS = frontSide.myLane.getPosition(pos);
        } else {
            pos = backSide.getPos() + length / 2;
            posGPS = backSide.myLane.getPosition(pos);
        }
        posGPS.setTime(time);
        return posGPS;
    }


}
