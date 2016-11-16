package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class RoadManager {

    public static Logger LOG = LoggerFactory.getLogger(RoadManager.class);


    private Map<Position, List<RoadSection>> RoadEdges;
    private List<RoadSection> roadSections;
    private Map<UUID, Road> roads;
    private Map<Position, Intersection> intersectionMap;
    private List<EndRoadTrajectory> deadEnds;

    public RoadManager() {
        RoadEdges = new HashMap<>();
        roadSections = new ArrayList<>();
        deadEnds = new ArrayList<>();
        roads = new HashMap<>();
        intersectionMap = new HashMap<>();
    }

    @Deprecated
    public RoadSection addRoadSection(Position A, Position B) {
        Road R = new Road(UUID.randomUUID());
        return addRoadSection(A, B, R);
    }

    public RoadSection addRoadSection(Position A, Position B, Road myRoad) {
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
                deadEnds.add(new EndRoadTrajectory(source,destination,source.getRoadId()));
            }
        }
    }

    public int checkIntegrity(){
        int lanesProblems = 0;
        int intersectionProblems = 0;
        int deadEndProblems = 0;
        int trajectoryProblems = 0;
        Map<Trajectory,Boolean> trajectoryToCheck = new HashMap<>();

        //check integrity for simple trajectories

        for (RoadSection r : roadSections) {
            lanesProblems += checkLane(r.getLaneAB());
            lanesProblems += checkLane(r.getLaneBA());

            trajectoryToCheck.put(r.getLaneAB().getInsertTrajectory(),false);
            trajectoryToCheck.put(r.getLaneBA().getInsertTrajectory(),false);
        }
        LOG.debug("integrity check result for RoadSections(" + roadSections.size() + "): " + lanesProblems);

        for (Intersection i : intersectionMap.values()){
            List<AdvancedTrajectory> trajectories = i.getTrajectories();
            if (trajectories.size() == 0){
                intersectionProblems++;
                LOG.error("Intersection have no trajectories");
            }
            for (AdvancedTrajectory trajectory : trajectories){
                if(trajectory.getLength()<0){
                    intersectionProblems++;
                    LOG.error("AdvancedTrajectory length negative");
                }

                if(trajectory.getSource()==null){
                    intersectionProblems++;
                    LOG.error("AdvancedTrajectory source is null");
                }

                if(trajectory.getDestination()==null){
                    intersectionProblems++;
                    LOG.error("AdvancedTrajectory destination is null");
                }

                trajectoryToCheck.put(trajectory,false);
            }
        }
        LOG.debug("integrity check result for Intersections(" + intersectionMap.size() + "): " + intersectionProblems);

        for (EndRoadTrajectory trajectory : deadEnds){
            if(trajectory.getLength()<0){
                deadEndProblems++;
                LOG.error("EndRoadTrajectory length negative");
            }

            if(trajectory.getSource()==null){
                deadEndProblems++;
                LOG.error("EndRoadTrajectory source is null");
            }

            if(trajectory.getDestination()==null){
                deadEndProblems++;
                LOG.error("EndRoadTrajectory destination is null");
            }

            trajectoryToCheck.put(trajectory,false);
        }
        LOG.debug("integrity check result for DeadEnds(" + deadEnds.size() + "): " + deadEndProblems);

        if(intersectionProblems == 0 && lanesProblems == 0 && deadEndProblems == 0 ){
            trajectoryProblems = checkTrajectoryAccess(trajectoryToCheck);
        }
        //FIXME
        trajectoryProblems = 0;

        return intersectionProblems+lanesProblems+deadEndProblems+trajectoryProblems;
    }

    private int checkLane(Lane lane){
        int problem = 0;
        if(lane.getInsertTrajectory().getLength()<=0 || Double.isNaN(lane.getInsertTrajectory().getLength())){
            problem++;
            LOG.error("trajectory is negative or NaN");
        }

        if(lane.getInsertTrajectory().getDestinationType() == TrajectoryType.Undefined){
            problem++;
            LOG.error("trajectory destination type is undefined");
        }

        if(lane.getInsertTrajectory().getDestinationsTrajectories().size()==0){
            problem++;
            LOG.error("trajectory have no destination");
        }

        for (Trajectory trajectory : lane.getInsertTrajectory().getSourcesTrajectories().values()) {
            if(trajectory == null){
                problem ++;
                LOG.error("trajectory have null destination");
            }
        }
        return problem;
    }

    private int checkTrajectoryAccess(Map<Trajectory,Boolean> trajectoryMap){
        int notExploredTrajetories = 0;
        LOG.debug("Total trajectories: " + trajectoryMap.size());
        Trajectory start = (Trajectory) trajectoryMap.keySet().toArray()[0];
        try {
            start.explore(trajectoryMap);
        }catch (StackOverflowError e){
            return 1;
        }


        for(boolean explored : trajectoryMap.values()){
            if (!explored){
                notExploredTrajetories++;
            }
        }
        LOG.debug(" trajectories not reached: " + notExploredTrajetories);

        return notExploredTrajetories;
    }

}
