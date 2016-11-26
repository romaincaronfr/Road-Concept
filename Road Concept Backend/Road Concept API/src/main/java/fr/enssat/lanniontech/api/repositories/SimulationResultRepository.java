package fr.enssat.lanniontech.api.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SimulationResultRepository extends SimulationRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationResultRepository.class);

    // ==========
    // CONGESTION
    // ==========

    public void addRoadMetric(UUID simulationUUID, UUID roadId, int congestion) {
        //TODO
    }

    // ========
    // VEHICLES
    // ========

    public void addVehicleInfo(UUID simulationUUID, int id, long time, double angle) {
        //TODO
    }

    // ======
    // COMMON
    // ======

    public void delete(UUID simulationUUID){
        // TODO
    }

}
