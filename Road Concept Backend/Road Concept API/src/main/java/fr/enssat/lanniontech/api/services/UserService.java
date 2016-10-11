package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.entities.UserType;
import fr.enssat.lanniontech.api.exceptions.InvalidParameterException;
import fr.enssat.lanniontech.api.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private UserRepository repository = new UserRepository();

    public User create(String email, String password, String lastName, String firstName, UserType type) {
        if (!isValidEmailAddress(email)) {
            throw new InvalidParameterException("The email parameter has an incorrect format.");
        }
        String securePassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = repository.create(email, lastName, firstName, securePassword, type);
        LOGGER.debug("User has been created with id=" + user.getId());
        return user;
    }

    private boolean isValidEmailAddress(String email) {
        String format = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern pattern = java.util.regex.Pattern.compile(format);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public User get(String email) {
        return repository.getFromEmail(email);
    }

    public boolean delete(User user) {
        int count = repository.delete(user);
        return count == 1; // // If false, something goes wrong (0 or more than 1 rows deleted)
    }

    public List<User> getAll() throws SQLException {
        return repository.getAll();
    }
}
