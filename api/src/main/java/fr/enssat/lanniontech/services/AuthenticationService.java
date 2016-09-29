package fr.enssat.lanniontech.services;

import fr.enssat.lanniontech.entities.User;
import fr.enssat.lanniontech.exceptions.AuthenticationException;

public class AuthenticationService {

    public User login(String login, String password) throws AuthenticationException {
        //TODO check the db
        return new User(login);
    }
}
