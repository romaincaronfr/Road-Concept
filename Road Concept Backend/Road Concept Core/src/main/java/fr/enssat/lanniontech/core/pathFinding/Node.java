package fr.enssat.lanniontech.core.pathFinding;

import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;

import java.util.UUID;

public class Node {
    private double heuristic;
    private double cost;
    private UUID source;
    private Node sourceNode;
    private Intersection intersection;

    public Node(double cost, double heuristic, UUID source, Node sourceNode, Intersection intersection) {
        this.cost = cost;
        this.heuristic = heuristic;
        this.source = source;
        this.sourceNode = sourceNode;
        this.intersection = intersection;
    }

    public boolean betterThan(Node node) {
        return (getHeuristic() + getCost()) <= (node.getHeuristic() + node.getCost());
    }

    public double getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public UUID getSource() {
        return source;
    }

    public void setSource(UUID source) {
        this.source = source;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }
}
