package fr.enssat.lanniontech.api.repositories;

import fr.enssat.lanniontech.api.entities.User;

public abstract class MapRepository extends AbstractRepository {

    public boolean canAccessMap(User user, int mapID) {
        // TODO
        return true;
    }
}
