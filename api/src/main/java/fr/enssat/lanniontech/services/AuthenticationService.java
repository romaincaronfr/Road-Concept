package fr.enssat.lanniontech.services;

public class AuthenticationService {

    public boolean login(String login, String password) {
        if (login != null && login != "" && password != null && password != "") {
            return true;
        }
        return false;
    }
}
