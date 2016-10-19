package fr.enssat.lanniontech.api.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class MapInfo implements SQLStoredEntity {

    private int id;
    @JsonProperty(access = Access.WRITE_ONLY)
    private int userID;
    private String name;
    private String imageURL;
    private String description;
    @JsonProperty(access = Access.WRITE_ONLY)
    private boolean fromOSM;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public Object getIdentifierValue() {
        return getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getIdentifierName() {
        return "id";
    }

    public boolean isFromOSM() {
        return fromOSM;
    }

    public void setFromOSM(boolean fromOSM) {
        this.fromOSM = fromOSM;
    }
}
