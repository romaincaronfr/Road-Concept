package fr.enssat.lanniontech;

import fr.enssat.lanniontech.positioning.Position;
import fr.enssat.lanniontech.roadElements.Road;
import fr.enssat.lanniontech.vehicleElements.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


public class Simulator implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(Simulator.class);

    private RoadCreator RC;
    private double progress;
    private Thread simulatorThread;
    private double length;
    private double precision;
    private ArrayList<Vehicle> vehicles;
    private ReentrantReadWriteLock l;
    private long startTime;
    private long stopTime;

    public Simulator(){
        Position A = new Position(40,0);
        Position B = new Position(40,0.01);
        Position C = new Position(40.01,0.02);
        Position D = new Position(40.02,0.02);

        vehicles = new ArrayList<Vehicle>();

        l = new ReentrantReadWriteLock();

        RC = new RoadCreator();

        Road R= RC.addRoad(A,B,42);
        RC.addRoadSectionToRoad(B,C,42);
        RC.addRoadSectionToRoad(C,D,42);

        progress=0;

        vehicles.add(new Vehicle(1,R.get(0).getLaneA(),0,1,40));
        vehicles.add(new Vehicle(2,R.get(1).getLaneA(),100,1,20));
        vehicles.add(new Vehicle(3,R.get(2).getLaneB(),0,1,40));
        vehicles.add(new Vehicle(3,R.get(1).getLaneB(),0,1,10));
        vehicles.add(new Vehicle(3,R.get(0).getLaneB(),0,1,30));




        simulatorThread = new Thread(this);
    }

    public boolean launchSimulation(double length,double precision){
        if(simulatorThread.isAlive()){
            return false;
        }else {
            this.length = length;
            this.precision = precision;
            startTime = System.currentTimeMillis();
            stopTime = 0;
            simulatorThread.start();
            return true;
        }
    }

    public void run(){
        long step = (int)(length/precision);
        WriteLock wl = l.writeLock();
        for (long i = 0; i < step ; i++) {

            for (int j = 0; j < vehicles.size(); j++) {
                vehicles.get(j).updateAcceleration();
            }

            for (int j = 0; j < vehicles.size() ; j++) {
                vehicles.get(j).updatePos(precision);
            }

            wl.lock();
            try{
                progress = (double)i/step;
            }finally {
                wl.unlock();
            }
        }
        stopTime = System.currentTimeMillis();
    }

    public double getProgress(){
        double progress;
        ReadLock rl = l.readLock();
        rl.lock();
        try{
            progress = this.progress;
        }finally {
            rl.unlock();
        }

        return progress;
    }

    public long getDuration(){
        if(startTime  == 0){
            return 0;
        }else if(stopTime == 0){
            return System.currentTimeMillis()-startTime;
        }else {
            return stopTime-startTime;
        }
    }

    public void waitForEnd(){
        try {
            simulatorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args){
        Simulator Sim = new Simulator();

        Sim.launchSimulation(24*3600,0.1);

        Sim.waitForEnd();

        System.out.println(Sim.getDuration());
    }

    public static int foo() {
        LOG.debug("foo() returns 4");
        return 4;
    } //For junit test
}
