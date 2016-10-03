package fr.enssat.lanniontech.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User implements Entity {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private int id;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
