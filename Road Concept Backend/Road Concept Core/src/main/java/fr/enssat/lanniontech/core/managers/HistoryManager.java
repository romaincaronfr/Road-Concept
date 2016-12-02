package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.positioning.SpaceTimePosition;
import fr.enssat.lanniontech.core.roadElements.RoadMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HistoryManager extends Observable {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryManager.class);

    //structure: <VehicleId,<timestamp,Position>>
    private List<List<SpaceTimePosition>> positionHistoryFiFo;
    private List<List<RoadMetrics>> roadMetricsHistoryFiFo;
    private List<SpaceTimePosition> currentPositionSample;
    private List<RoadMetrics> currentRoadMetricsSample;
    private ReentrantReadWriteLock lock;
    private PositionManager positionManager;


    public HistoryManager(PositionManager positionManager) {
        lock = new ReentrantReadWriteLock();
        positionHistoryFiFo = new ArrayList<>();
        roadMetricsHistoryFiFo = new ArrayList<>();
        currentPositionSample = new ArrayList<>();
        currentRoadMetricsSample = new ArrayList<>();
        this.positionManager = positionManager;
    }

    public void addPosition(SpaceTimePosition P) {
        if (!positionManager.isInRange(P)) {
            LOGGER.debug("car out of bounds");
        }
        currentPositionSample.add(P);
    }

    public void addRoadMetric(RoadMetrics R) {
        currentRoadMetricsSample.add(R);
    }

    public List<RoadMetrics> getRoadMetricsSample() {
        List<RoadMetrics> sample = null;
        try {
            lock.readLock().lock();
            sample = roadMetricsHistoryFiFo.get(0);
        } finally {
            lock.readLock().unlock();
        }
        return sample;
    }

    public List<SpaceTimePosition> getPositionSample() {
        List<SpaceTimePosition> sample = null;
        try {
            lock.readLock().lock();
            sample = positionHistoryFiFo.get(0);
        } finally {
            lock.readLock().unlock();
        }
        return sample;
    }

    public void removeSamples() {
        try {
            lock.writeLock().lock();
            positionHistoryFiFo.remove(0);
            roadMetricsHistoryFiFo.remove(0);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void commitChanges(UUID simId) {
        try {
            lock.writeLock().lock();
            positionHistoryFiFo.add(currentPositionSample);
            roadMetricsHistoryFiFo.add(currentRoadMetricsSample);
        } finally {
            lock.writeLock().unlock();
        }
        currentRoadMetricsSample = new ArrayList<>();
        currentPositionSample = new ArrayList<>();
        setChanged();
        notifyObservers(simId);
    }

}
