package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository repository = new UserRepository();

    public User create(String email, String password, String lastName, String firstName, UserType type) {
        String securePassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = repository.create(email, lastName, firstName, securePassword, type);
        LOGGER.debug("User has been created with id=" + user.getId());
        return user;
    }

    public User get(String email) {
        return repository.getFromEmail(email);
    }

    public boolean delete(User user) {
        int count = repository.delete(user);
        return count == 1; // // If false, something goes wrong (0 or more than 1 rows deleted)
    }

    public List<User> getAll() {
        return repository.getAll();
    }
}
