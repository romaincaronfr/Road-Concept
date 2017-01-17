package fr.enssat.lanniontech.core.roadElements.roads;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.RoadMetrics;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Road {
    private UUID id;
    private double length;
    private Position A;
    private Position B;
    private int maxSpeed;
    protected List<RoadSection> sections;

    public Road(UUID id, int maxSpeed, boolean oneWay) {
        this.id = id;
        this.maxSpeed = maxSpeed;
        this.sections = new ArrayList<>();
        this.length = 0;
        A = null;
        B = null;
    }

    private void updateRoad() {
        length = 0;
        for (RoadSection section : sections) {
            length += section.getLength();
        }
    }

    public void addSection(RoadSection newSection) {
        if (A == null && B == null) {
            A = newSection.getA();
            B = newSection.getB();
            sections.add(newSection);
        } else if (A == newSection.getA()) {
            A = newSection.getB();
            sections.add(newSection);
        } else if (A == newSection.getB()) {
            A = newSection.getA();
            sections.add(newSection);
        } else if (B == newSection.getA()) {
            B = newSection.getB();
            sections.add(sections.size(), newSection);
        } else if (B == newSection.getB()) {
            B = newSection.getA();
            sections.add(sections.size(), newSection);
        } else return;
        updateRoad();
    }

    public int size() {
        return sections.size();
    }

    public UUID getId() {
        return id;
    }

    public Position getA() {
        return A;
    }

    public Position getB() {
        return B;
    }

    public double getLength() {
        return length;
    }

    public RoadMetrics getMetrics(int timestamp) {
        return new RoadMetrics(id, timestamp, getCongestion());
    }

    public abstract int getCongestion();

    public abstract RoadSection get(int i);

    public int getMaxSpeed() {
        return maxSpeed;
    }
}
