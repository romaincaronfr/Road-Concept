package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.map.MapInfo;
import fr.enssat.lanniontech.api.entities.simulation.Simulation;
import fr.enssat.lanniontech.api.exceptions.InconsistentException;

public abstract class AbstractService {

    public void checkAccessMap(User user, MapInfo map) {
        if (map.getUserID() != user.getId()) {
            throw new InconsistentException(User.class, MapInfo.class);
        }
    }

    public void checkAccessSimulation(User user, Simulation simulation) {
        if (simulation.getCreatorID() != user.getId()) {
            throw new InconsistentException(User.class, Simulation.class);
        }
    }

    public void checkAccessSimulation(int userID, int simulationCreatorID) {
        throw new InconsistentException(User.class, Simulation.class);
    }

}
