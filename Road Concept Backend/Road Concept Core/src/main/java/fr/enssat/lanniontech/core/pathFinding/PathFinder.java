package fr.enssat.lanniontech.core.pathFinding;


import fr.enssat.lanniontech.core.managers.RoadManager;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.AdvancedTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PathFinder {

    public static Logger LOG = LoggerFactory.getLogger(PathFinder.class);

    private RoadManager roadManager;
    private Random gen;

    public PathFinder(RoadManager RM){
        roadManager = RM;
        gen = new Random();
    }

    public Path getRandomPath(Trajectory source, int length){
        Path myPath = new Path();
        UUID step = source.getRoadId();
        LOG.debug("starting road : " + step);
        myPath.addToPath(step);
        Intersection nextInter = source.getNextIntersection();
        List<AdvancedTrajectory> possibleNext;

        for (int i = 0; i < length; i++) {
            possibleNext = nextInter.getTrajectoriesFrom(step);

            if(possibleNext.size()==1){
                step=possibleNext.get(0).getRoadId();
                nextInter = possibleNext.get(0).getNextIntersection();
            }else{
                int next = gen.nextInt(possibleNext.size());
                step=possibleNext.get(next).getRoadId();
                nextInter = possibleNext.get(next).getNextIntersection();
            }
            myPath.addToPath(step);
            LOG.debug("next road : " + step);
        }

        return myPath;
    }

    public Path getPathTo(Trajectory source, UUID destination, boolean reverse){
        Path myPath = new Path();

        Intersection start = source.getNextIntersection();
        Intersection goal = null;
        if(reverse){
            goal = roadManager.getIntersection(roadManager.getRoad(destination).getB());
            if(goal == null){
                goal = roadManager.getIntersection(roadManager.getRoad(destination).getA());
            }
        }else{
            goal = roadManager.getIntersection(roadManager.getRoad(destination).getA());
            if(goal == null){
                goal = roadManager.getIntersection(roadManager.getRoad(destination).getB());
            }
        }

        List<Node> openNodes = new LinkedList<>();
        Map<Intersection,Node> closedNodes = new HashMap<>();
        Node finalNode = null;

        Node node = new Node(0, Position.length(start.getP(),goal.getP()),source.getRoadId(),null,start);
        openNodes.add(node);

        while(openNodes.size()>0 && finalNode==null){
            node = openNodes.get(0);
            if (closedNodes.containsKey(node.intersection) && closedNodes.get(node.intersection).cost >= node.cost) {
                openNodes.remove(node);
            } else {
                for (Trajectory t : node.intersection.getTrajectoriesFrom(node.source)) {
                    if (t.getNextIntersection() != node.intersection) {
                        Node newNode = new Node(node.cost + roadManager.getRoad(t.getRoadId()).getLength(),
                                Position.length(t.getNextIntersection().getP(), goal.getP()), t.getRoadId(),
                                node, t.getNextIntersection());
                        int pos = 0;
                        while(pos<openNodes.size() && !newNode.betterThan(openNodes.get(pos))){
                            pos++;
                        }
                        openNodes.add(pos,newNode);
                    }


                    if(t.getRoadId().equals(destination)  && node.intersection == goal){
                        finalNode = node;
                    }
                }
                openNodes.remove(node);
                closedNodes.replace(node.intersection, node);
            }
        }
        
        if(finalNode == null){
            LOG.error("destination unreachable");
        }

        reconstructPath(myPath,finalNode);
        myPath.addToPath(destination);
        LOG.debug(myPath.toString());

        return myPath;
    }

    private void reconstructPath(Path myPath,Node node){
        if(node.sourceNode != null){
            reconstructPath(myPath,node.sourceNode);
        }
        myPath.addToPath(node.source);
    }

}
