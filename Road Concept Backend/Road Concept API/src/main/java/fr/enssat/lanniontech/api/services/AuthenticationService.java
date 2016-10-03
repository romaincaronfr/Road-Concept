package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.api.entities.User;
import fr.enssat.lanniontech.api.exceptions.AuthenticationException;

public class AuthenticationService {

    public User login(String login, String password) throws AuthenticationException {
        //TODO check the db
        User user = new User();
        user.setId(1);
        user.setUserName(login);
        return user;
    }
}
