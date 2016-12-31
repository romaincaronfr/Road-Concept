package fr.enssat.lanniontech.core.pathFinding;

import fr.enssat.lanniontech.core.trajectory.Trajectory;

import java.util.UUID;

public class Node {
    private double heuristic;
    private double cost;
    private Node sourceNode;
    private Trajectory trajectory;

    public Node(double cost, double heuristic, Node sourceNode, Trajectory trajectory) {
        this.cost = cost;
        this.heuristic = heuristic;
        this.sourceNode = sourceNode;
        this.trajectory = trajectory;
    }

    public boolean betterThan(Node node) {
        return (getHeuristic() + getCost()) <= (node.getHeuristic() + node.getCost());
    }

    public double getHeuristic() {
        return heuristic;
    }

    public double getCost() {
        return cost;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Trajectory getTrajectory() {
        return trajectory;
    }
}
