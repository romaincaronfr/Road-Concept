package fr.enssat.lanniontech.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


public class Simulator implements Runnable {
    public static Logger LOG = LoggerFactory.getLogger(Simulator.class);

    public RoadManager roadManager;
    public PositionManager positionManager;
    public VehicleManager vehicleManager;
    public static SimulationHistory simulationHistory;

    private double progress;
    private Thread simulatorThread;
    private double length;
    private double precision;
    private ReentrantReadWriteLock l;
    private long startTime;
    private long stopTime;

    public Simulator() {
        l = new ReentrantReadWriteLock();

        roadManager = new RoadManager();
        positionManager = new PositionManager();
        vehicleManager = new VehicleManager();
        simulationHistory = new SimulationHistory();

        progress = 0;

        simulatorThread = new Thread(this);
    }

    public boolean launchSimulation(double length, double precision) {
        if (simulatorThread.isAlive()) {
            return false;
        } else {
            this.length = length;
            this.precision = precision;
            startTime = System.currentTimeMillis();
            stopTime = 0;
            simulatorThread.start();
            return true;
        }
    }

    public void run() {
        long step = (long) (length / precision);
        WriteLock wl = l.writeLock();
        int j = 1;
        for (long i = 0; i < step; i++) {

            if (j == 10 / precision) {
                vehicleManager.newStep(precision, true);
                j = 1;
            } else {
                vehicleManager.newStep(precision, false);
                j++;
            }


            wl.lock();
            try {
                progress = (double) i / step;
            } finally {
                wl.unlock();
            }
        }
        progress = 1;
        stopTime = System.currentTimeMillis();
    }

    public double getProgress() {
        double progress;
        ReadLock rl = l.readLock();
        rl.lock();
        try {
            progress = this.progress;
        } finally {
            rl.unlock();
        }

        return progress;
    }

    public long getDuration() {
        if (startTime == 0) {
            return 0;
        } else if (stopTime == 0) {
            return System.currentTimeMillis() - startTime;
        } else {
            return stopTime - startTime;
        }
    }

    public void waitForEnd() {
        try {
            simulatorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
