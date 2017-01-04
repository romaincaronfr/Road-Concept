package fr.enssat.lanniontech.core;

import fr.enssat.lanniontech.core.managers.HistoryManager;
import fr.enssat.lanniontech.core.managers.PositionManager;
import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.managers.VehicleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


public class Simulator extends Observable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    private final RoadManager roadManager;
    private final PositionManager positionManager;
    private final VehicleManager vehicleManager;
    private final HistoryManager historyManager;

    private double progress;
    private Thread simulatorThread;
    private double length;
    private double precision;
    private ReentrantReadWriteLock l;
    private long startTime;
    private long stopTime;
    private int samplingRate;
    private UUID simId;

    public Simulator() {
        this(UUID.randomUUID());
    }

    public Simulator(UUID simId) {
        this.simId = simId;
        l = new ReentrantReadWriteLock();

        positionManager = new PositionManager();
        historyManager = new HistoryManager(positionManager);
        roadManager = new RoadManager();
        vehicleManager = new VehicleManager(historyManager, roadManager);

        progress = 0;

        simulatorThread = new Thread(this);
    }

    public boolean launchSimulation(double length, double precision, int samplingRate) {
        if (simulatorThread.isAlive()) {
            return false;
        } else {
          //  UncaughtExceptionHandler h = (Thread th, Throwable ex) -> {
          //      LOGGER.error("Exception in child thread");
          //      ex.printStackTrace();
          //  };
         //   simulatorThread.setUncaughtExceptionHandler(h);
            this.samplingRate = samplingRate;
            this.length = length;
            this.precision = precision;
            startTime = System.currentTimeMillis();
            simulatorThread.start();
            return true;
        }
    }

    @Override
    public void run() {
        try {
            long stepCycles = (long) (length / precision);
            WriteLock wl = l.writeLock();
            int j = samplingRate;
            int second = (int) (1 / precision);
            int k = second;
            int timestamp = -1;
            for (long i = 0; i < stepCycles; i++) {

                if (k == second) {
                    timestamp++;
                    vehicleManager.updateBuffers(timestamp);
                    k = 1;
                } else {
                    k++;
                }

                if (j == samplingRate) {
                    vehicleManager.newStep(precision, true, timestamp);
                    roadManager.saveSates(historyManager, timestamp);
                    historyManager.commitChanges(simId);
                    j = 1;
                } else {
                    vehicleManager.newStep(precision, false, timestamp);
                    j++;
                }

                try {
                    wl.lock();
                    progress = (double) i / stepCycles;
                } finally {
                    wl.unlock();
                }
            }
        } finally {
            setChanged();
            notifyObservers(simId);
            progress = 1;
            stopTime = System.currentTimeMillis();
            LOGGER.debug("Simulation " + simId + " ended after " + getDuration() + " ms");
        }
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

    public RoadManager getRoadManager() {
        return roadManager;
    }

    public PositionManager getPositionManager() {
        return positionManager;
    }

    public VehicleManager getVehicleManager() {
        return vehicleManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
