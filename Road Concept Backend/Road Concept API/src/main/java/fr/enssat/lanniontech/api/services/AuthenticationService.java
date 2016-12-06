package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.AuthenticationException;
import fr.enssat.lanniontech.api.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService extends AbstractService {

    private UserRepository userRepository = new UserRepository();

    public User login(String email, String password) {
        User user = userRepository.getFromEmail(email);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) { // login failed
            throw new AuthenticationException("Invalid credentials");
        }
        return user; // login success
    }

}
