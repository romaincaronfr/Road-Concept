package fr.enssat.lanniontech.core;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.EndRoadTrajectory;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RoadManager {
    private Map<Position, List<RoadSection>> RoadEdges;
    private List<RoadSection> roadSections;
    private Map<UUID, Road> roads;
    private Map<Position, Intersection> intersectionMap;

    public RoadManager() {
        RoadEdges = new HashMap<>();
        roadSections = new ArrayList<>();
        roads = new HashMap<>();
        intersectionMap = new HashMap<>();
    }

    @Deprecated
    public RoadSection addRoadSection(Position A, Position B) {
        return addRoadSection(A, B, null);
    }

    private RoadSection addRoadSection(Position A, Position B, Road myRoad) {
        RoadSection RS1 = new RoadSection(A, B, myRoad);

        roadSections.add(RS1);
        assembleRoadSections(RS1, A, myRoad);
        assembleRoadSections(RS1, B, myRoad);

        return RS1;
    }

    private void assembleRoadSections(RoadSection Rs1, Position P, Road myRoad) {
        if (RoadEdges.containsKey(P)) {
            if (RoadEdges.get(P).size() == 1 && RoadEdges.get(P).get(0).getMyRoad() == myRoad) {
                RoadSection Rs2 = RoadEdges.get(P).get(0);
                fuseRoadsSection(Rs1, Rs2, P);
                RoadEdges.remove(P);
            } else {
                RoadEdges.get(P).add(Rs1);
                if (!intersectionMap.containsKey(P)) {
                    intersectionMap.put(P, new Intersection(P));
                    for (RoadSection Rs : RoadEdges.get(P)) {
                        intersectionMap.get(P).addRoadSection(Rs);
                    }
                } else {
                    intersectionMap.get(P).addRoadSection(Rs1);
                }
            }
        } else {
            RoadEdges.put(P, new ArrayList<>());
            RoadEdges.get(P).add(Rs1);
        }
    }

    public Road addRoadSectionToRoad(Position A, Position B, UUID id) {
        Road R = roads.get(id);
        if (R == null) {
            R = new Road(id);
            roads.put(id, R);
        }
        R.addSection(addRoadSection(A, B, R));
        return R;
    }

    public Road getRoad(UUID id) {
        return roads.get(id);
    }

    public Intersection getIntersection(Position P) {
        return intersectionMap.get(P);
    }

    private void fuseRoadsSection(RoadSection RS1, RoadSection RS2, Position P) {
        RS2.getLeftLane(P).setNextLane(RS1.getRightLane(P));
        RS1.getLeftLane(P).setNextLane(RS2.getRightLane(P));
    }

    public void closeRoads(){
        for (Position P : RoadEdges.keySet() ){
            if(RoadEdges.get(P).size()==1){
                SimpleTrajectory source = RoadEdges.get(P).get(0).getLeftLane(P).getInsertTrajectory();
                SimpleTrajectory destination =RoadEdges.get(P).get(0).getRightLane(P).getInsertTrajectory();
                new EndRoadTrajectory(source,destination);
            }
        }
    }

    public boolean checkIntegrity(){
        boolean result = true;
        for (RoadSection r : roadSections) {
            if(r.getLaneAB().getInsertTrajectory().getLength()<=0){
                result = false;
            }
            if(r.getLaneBA().getInsertTrajectory().getLength()<=0){
                result = false;
            }
        }
        //todo check if all trajectories are accessible
        return result;
    }

}
