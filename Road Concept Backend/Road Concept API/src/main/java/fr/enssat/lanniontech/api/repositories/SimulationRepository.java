package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class SimulationRepository extends AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationRepository.class);

    public void duplicateFeatures(Simulation simulation) {
        //TODO: Utiliser mongodb.cloneCollection ou mongodb.copyTo
    }

    public void deleteAssociatedFeatures(UUID simulationUUID) {
        //TODO
    }
}
