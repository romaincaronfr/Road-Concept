package fr.enssat.lanniontech.services;

import fr.enssat.lanniontech.entities.User;
import org.apache.http.auth.AuthenticationException;

public class AuthenticationService {

    public User login(String login, String password) throws AuthenticationException{ //TODO: Create a project specific exception
        //TODO check the db
        return new User(login);
    }
}
