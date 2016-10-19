package fr.enssat.lanniontech.api.entities;

import fr.enssat.lanniontech.api.geojson.Point;

public class Couple {

    private Point from;
    private Point to;

    public Point getFrom() {
        return from;
    }

    public void setFrom(Point from) {
        this.from = from;
    }

    public Point getTo() {
        return to;
    }

    public void setTo(Point to) {
        this.to = to;
    }
}
