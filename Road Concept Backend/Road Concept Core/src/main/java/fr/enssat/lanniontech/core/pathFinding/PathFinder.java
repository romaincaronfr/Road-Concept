package fr.enssat.lanniontech.core.pathFinding;


import fr.enssat.lanniontech.core.exceptions.DestinationUnreachableException;
import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.OneWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;
import fr.enssat.lanniontech.core.roadElements.roads.OneWayRoad;
import fr.enssat.lanniontech.core.roadElements.roads.Road;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PathFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(PathFinder.class);

    private RoadManager roadManager;
    private Random gen;

    public PathFinder(RoadManager RM) {
        roadManager = RM;
        gen = new Random();
    }

    public Path getRandomPath(Trajectory source, int length) {
        Path myPath = new Path();
        UUID step = source.getRoadId();
        LOGGER.debug("starting road : " + step);
        myPath.addToPath(step);
        Intersection nextInter = source.getNextIntersection();
        List<SimpleTrajectory> possibleNext;

        for (int i = 0; i < length; i++) {
            possibleNext = nextInter.getTrajectoriesFrom(step);

            if (possibleNext.size() == 1) {
                step = possibleNext.get(0).getRoadId();
                nextInter = possibleNext.get(0).getNextIntersection();
            } else {
                int next = gen.nextInt(possibleNext.size());
                step = possibleNext.get(next).getRoadId();
                nextInter = possibleNext.get(next).getNextIntersection();
            }
            myPath.addToPath(step);
        }

        return myPath;
    }

    public Path getPathTo(Trajectory source, UUID destination, boolean reverse) {
        Path myPath = new Path();

        Trajectory goal;

        Road destinationRoad = roadManager.getRoad(destination);
        RoadSection roadSection = destinationRoad.get(destinationRoad.size()/2);
        if(roadSection instanceof OneWayRoadSection){
            goal = ((OneWayRoadSection)roadSection).getLane().getInsertTrajectory();
        }else {
            if(reverse){
                goal = ((DualWayRoadSection)roadSection).getLaneBA().getInsertTrajectory();
            }else {
                goal = ((DualWayRoadSection)roadSection).getLaneAB().getInsertTrajectory();
            }
        }

        List<Node> openNodes = new LinkedList<>();
        Map<Trajectory, Node> closedNodes = new HashMap<>();
        Node finalNode = null;

        Node node = new Node(0, Position.length(source.getPosition(), goal.getPosition()),
                null,source);
        openNodes.add(node);

        while (!openNodes.isEmpty() && finalNode == null) {
            //recupere le node le plus proche
            node = openNodes.get(0);
            openNodes.remove(node);
            //pour toute les tajectoire de ce node
            for (TrajectoryJunction tj : node.getTrajectory().getAllNext()) {
                Trajectory t = tj.getDestination();
                //on verifie que le node n'a pas deja été exploré
                if(!closedNodes.keySet().contains(t)){
                    //calcul du cout du node
                    double cost = node.getCost() + t.getLength();
                    //creation du node
                    Node newNode = new Node(cost, Position.length(t.getPosition(), goal.getPosition()),
                            node, t);
                    //si la destination est la meme que la trajectoire
                    if(t==goal){
                        finalNode = newNode;
                    }else{
                        int pos = 0;
                        while (pos < openNodes.size() && !newNode.betterThan(openNodes.get(pos))) {
                            pos++;
                        }
                        openNodes.add(pos, newNode);
                    }
                }
            }
            closedNodes.put(node.getTrajectory(),node);
        }

        if (finalNode == null) {
            LOGGER.error("destination unreachable");
            throw new DestinationUnreachableException();
        }

        reconstructPath(myPath, finalNode);
        //myPath.addToPath(destination);

        return myPath;
    }

    private void reconstructPath(Path myPath, Node node) {
        if (node.getSourceNode() != null) {
            reconstructPath(myPath, node.getSourceNode());
        }
        myPath.addToPath(node.getTrajectory().getRoadId());
    }

}
