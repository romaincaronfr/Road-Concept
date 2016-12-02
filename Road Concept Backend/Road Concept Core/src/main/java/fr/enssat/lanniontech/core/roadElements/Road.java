package fr.enssat.lanniontech.core.roadElements;

import fr.enssat.lanniontech.core.positioning.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Road {
    private UUID id;
    private double maxSpeed;
    private List<RoadSection> sections;
    private double length;
    private double timeWeigth;
    private Position A;
    private Position B;

    public Road(UUID id) {
        this(id, 50);
    }

    public Road(UUID id, double maxSpeed) {
        this.id = id;
        this.maxSpeed = maxSpeed;
        this.sections = new ArrayList<>();
        this.length = 0;
        this.timeWeigth = 0;
        A = null;
        B = null;
    }

    private void updateRoad() {
        length = 0;
        for (RoadSection section : sections) {
            length += section.getLength();
        }
        timeWeigth = length / maxSpeed;
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

    public RoadSection get(int i) {
        return sections.get(i);
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
        return new RoadMetrics(id, timestamp, 0);
    }

    private int getCongestion(){
        double congestion = 0;
        for (RoadSection section : sections){
            congestion = Math.max(congestion,section.getCongestion());
        }
        return (int) congestion;
    }
}
