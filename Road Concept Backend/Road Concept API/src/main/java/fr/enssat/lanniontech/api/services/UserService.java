package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService extends AbstractRoadConceptService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository repository = new UserRepository();

    public User create(String email, String password, String lastName, String firstName, UserType type) {
        User user = repository.create(email, lastName, firstName, password, type);
        LOGGER.debug("User has been created with id=" + user.getId());
        return user;
    }

    public User get(String email) {
        User user = repository.getFromEmail(email);
        return user;
    }
}
