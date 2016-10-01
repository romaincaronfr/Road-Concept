package fr.enssat.lanniontech.jsonparser.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties({"_id"}) // Auto set by MongoDB
public class Map {

    @JsonProperty(value = "mapID")
    private int id;
    @JsonProperty(value = "objects")
    private List<MapElement> elements;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MapElement> getElements() {
        return elements;
    }

    public void setElements(List<MapElement> elements) {
        this.elements = elements;
    }

}
