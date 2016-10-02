package fr.enssat.lanniontech;

import fr.enssat.lanniontech.positioning.Position;
import fr.enssat.lanniontech.roadElements.Road;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


public class Simulator implements Runnable {

    private static Logger LOG = LoggerFactory.getLogger(Simulator.class);

    private RoadManager roadManager;
    private PositionManager positionManager;
    private VehicleManager vehicleManager;

    private double progress;
    private Thread simulatorThread;
    private double length;
    private double precision;
    private ReentrantReadWriteLock l;
    private long startTime;
    private long stopTime;

    public Simulator(){
        l = new ReentrantReadWriteLock();

        roadManager = new RoadManager();
        positionManager = new PositionManager();
        vehicleManager = new VehicleManager();


        Position A = positionManager.addPosition(40,0);
        Position B = positionManager.addPosition(40,0.1);
        Position C = positionManager.addPosition(40.1,0.1);
        Position D = positionManager.addPosition(40.1,0);

        Road R= roadManager.addRoad(A,B,0);
        roadManager.addRoadSectionToRoad(B,C,0);
        roadManager.addRoadSectionToRoad(C,D,0);
        //roadManager.addRoad(D,A,1);

        vehicleManager.addToSpawnArea(R);

        for (int i = 0; i < 100; i++) {
            System.out.println(vehicleManager.addVehicle());
        }


        progress=0;

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
        long step = (long)(length/precision);
        WriteLock wl = l.writeLock();
        for (long i = 0; i < step ; i++) {

            vehicleManager.newStep(precision);

            wl.lock();
            try{
                progress = (double)i/step;
            }finally {
                wl.unlock();
            }
        }
        progress = 1;
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

        while(Sim.getProgress()<1){
            System.out.println("Sim time: " + Sim.getDuration());
            System.out.println("Sim progress: " + 100*Sim.getProgress());
            System.out.println("------------------------------------------------");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Sim.waitForEnd();

        System.out.println(Sim.getDuration());
    }
}
