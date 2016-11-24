package fr.enssat.lanniontech.core.pathFinding;

import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;

import java.util.UUID;

public class Node {
    double heuristic;
    double cost;
    UUID source;
    Node sourceNode;
    Intersection intersection;

    public Node(double cost, double heuristic, UUID source, Node sourceNode, Intersection intersection) {
        this.heuristic = heuristic;
        this.cost = cost;
        this.source = source;
        this.sourceNode = sourceNode;
        this.intersection = intersection;
    }

    public boolean betterThan(Node node) {
        return (heuristic + cost) <= (node.heuristic + node.cost);
    }
}
