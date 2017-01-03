package fr.enssat.lanniontech.roadconceptandroid.Entities;

/**
 * Created by Romain on 20/12/2016.
 */

public class Map {

    private int id;
    private String name;
    private String imageURL;
    private String description;

    public Map() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
