package fr.enssat.lanniontech.api.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SimulationRepository extends AbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationRepository.class);

    // TODO: Stockage SQL (définir schéma SQL)
    // TODO: Dupliquer la map associé lors de la création d'une simulation. Comment gérer l'ID de la map ?
    // TODO: Voir FFNEXParser pour le cloneur
}
