package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository repository = new UserRepository();

    public User create(String email, String password, String lastName, String firstName, UserType type) {
        String securePassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = repository.create(email, lastName, firstName, securePassword, type);
        LOGGER.debug("User has been created with id=" + user.getId());
        LOGGER.debug("Original password was [" + password + "] and secure password is [" + securePassword + "]");
        return user;
    }

    public User get(String email) {
        return repository.getFromEmail(email);
    }
}
