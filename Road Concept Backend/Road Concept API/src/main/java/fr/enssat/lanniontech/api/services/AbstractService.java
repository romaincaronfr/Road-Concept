package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.MapInfo;
import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.UnconsistentException;

public abstract class AbstractService {

    public void checkAccessMap(User user, MapInfo map) {
        if (map.getUserID() != user.getId()) {
            throw new UnconsistentException(User.class, MapInfo.class);
        }
    }
}
