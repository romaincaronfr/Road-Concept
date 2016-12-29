package fr.enssat.lanniontech.core.managers;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Lane;
import fr.enssat.lanniontech.core.roadElements.roadSections.DualWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roadSections.OneWayRoadSection;
import fr.enssat.lanniontech.core.roadElements.roads.DualWayRoad;
import fr.enssat.lanniontech.core.roadElements.roads.OneWayRoad;
import fr.enssat.lanniontech.core.roadElements.roads.Road;
import fr.enssat.lanniontech.core.roadElements.roadSections.RoadSection;
import fr.enssat.lanniontech.core.roadElements.intersections.Intersection;
import fr.enssat.lanniontech.core.trajectory.EndRoadTrajectory;
import fr.enssat.lanniontech.core.trajectory.SimpleTrajectory;
import fr.enssat.lanniontech.core.trajectory.Trajectory;
import fr.enssat.lanniontech.core.trajectory.TrajectoryEndType;
import fr.enssat.lanniontech.core.trajectory.TrajectoryJunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RoadManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoadManager.class);

    private Map<Position, List<RoadSection>> roadEdges;
    private List<RoadSection> roadSections;
    private Map<UUID, Road> roads;
    private Map<Position, Intersection> intersectionMap;
    private List<EndRoadTrajectory> deadEnds;

    public RoadManager() {
        roadEdges = new HashMap<>();
        roadSections = new ArrayList<>();
        deadEnds = new ArrayList<>();
        roads = new HashMap<>();
        intersectionMap = new HashMap<>();
    }

    /**
     * create and assemble the road section to its Road
     */
    public RoadSection addDualWayRoadSection(Position A, Position B, Road myRoad) {
        DualWayRoadSection RS = new DualWayRoadSection(A, B, myRoad);

        roadSections.add(RS);
        assembleDualWayRoadSections(RS, A, myRoad);
        assembleDualWayRoadSections(RS, B, myRoad);

        return RS;
    }

    private RoadSection addOneWayRoadSection(Position A, Position B, Road myRoad){
        OneWayRoadSection RS = new OneWayRoadSection(A, B, myRoad);

        roadSections.add(RS);
        assembleOneWayRoadSections(RS, A, myRoad);
        assembleOneWayRoadSections(RS, B, myRoad);

        return RS;
    }

    /**
     * Create and add a RoadSection from the specified positions to the specified Road
     */
    public Road addRoadSectionToRoad(Position A, Position B, UUID id, int maxSpeed, boolean oneWay) {
        Road R;
        if(oneWay){
            R = roads.computeIfAbsent(id, k -> new OneWayRoad(id, maxSpeed));
            R.addSection(addOneWayRoadSection(A,B,R));
        }else {
            R = roads.computeIfAbsent(id, k -> new DualWayRoad(id, maxSpeed));
            R.addSection(addDualWayRoadSection(A, B, R));
        }
        return R;
    }

    /**
     * assemble the new RoadSection to the Road on the Position
     */
    private void assembleDualWayRoadSections(RoadSection Rs1, Position P, Road myRoad) {
        if (roadEdges.containsKey(P)) {
            if (roadEdges.get(P).size() == 1 && roadEdges.get(P).get(0).getMyRoad() == myRoad) {
                RoadSection Rs2 = roadEdges.get(P).get(0);
                fuseDualWayRoadSection((DualWayRoadSection)Rs1, (DualWayRoadSection)Rs2, P);
                roadEdges.remove(P);
            } else {
                roadEdges.get(P).add(Rs1);
            }
        } else {
            roadEdges.put(P, new ArrayList<>());
            roadEdges.get(P).add(Rs1);
        }
    }

    private void assembleOneWayRoadSections(OneWayRoadSection Rs1, Position P, Road myRoad) {
        if (roadEdges.containsKey(P)) {
            if (roadEdges.get(P).size() == 1 && roadEdges.get(P).get(0).getMyRoad() == myRoad) {
                OneWayRoadSection Rs2 = (OneWayRoadSection) roadEdges.get(P).get(0);
                fuseOneWayRoadSection(Rs1, Rs2, P);
                roadEdges.remove(P);
            } else {
                roadEdges.get(P).add(Rs1);
            }
        } else {
            roadEdges.put(P, new ArrayList<>());
            roadEdges.get(P).add(Rs1);
        }
    }

    /**
     * assemble the lanes of the both roadsections RS1 & RS2 on the position P
     */
    private void fuseDualWayRoadSection(DualWayRoadSection RS1, DualWayRoadSection RS2, Position P) {
            RS1.getOutputLane(P).setNextLane(RS2.getInputLane(P));
            RS2.getOutputLane(P).setNextLane(RS1.getInputLane(P));
            assembleLanes(RS1.getOutputLane(P), RS2.getInputLane(P));
            assembleLanes(RS2.getOutputLane(P), RS1.getInputLane(P));
    }

    private void fuseOneWayRoadSection(OneWayRoadSection RS1, OneWayRoadSection RS2, Position P) {
        Lane L1 = RS1.getOutputLane(P);
        Lane L2 = RS2.getInputLane(P);
        if(L1 == null){
            L1 = RS2.getOutputLane(P);
            L2 = RS1.getInputLane(P);
        }

        L1.setNextLane(L2);
        assembleLanes(L1, L2);
    }

    //TRAJECTORIES ASSEMBLY

    /**
     * check all the roadEdges and crete the right end Type (DeadEnd or Intersection)
     */
    public void closeRoads() {
        for (Position P : roadEdges.keySet()) {
            if (roadEdges.get(P).size() == 1) {
                RoadSection r = roadEdges.get(P).get(0);
                if(r instanceof OneWayRoadSection) {
                    transformAsDualWayRoad(r.getMyRoad());
                }
            }
        }

        for (Position P : roadEdges.keySet()) {
            if (roadEdges.get(P).size() == 1) {
                closeEdge(P);
            }else{
                createIntersection(P);
            }
        }
    }

    private void createIntersection(Position P) {
        Intersection intersection = new Intersection(P);
        for (RoadSection section : roadEdges.get(P)) {
            intersection.addRoadSection(section);
        }
        intersection.assembleIntersection();
        intersectionMap.put(P, intersection);
    }

    private void closeEdge(Position P) {
        RoadSection r = roadEdges.get(P).get(0);
        SimpleTrajectory source = r.getOutputLane(P).getInsertTrajectory();
        SimpleTrajectory destination = r.getInputLane(P).getInsertTrajectory();
        EndRoadTrajectory deadEnd = new EndRoadTrajectory(source, destination, source.getRoadId());

        TrajectoryJunction junction = new TrajectoryJunction(source, deadEnd, source.getStop(), 0);
        source.addDestination(junction);
        deadEnd.setSource(junction);

        junction = new TrajectoryJunction(deadEnd, destination, deadEnd.getLength(), destination.getStart());
        destination.addSource(junction);
        deadEnd.setDestination(junction);

        source.setDestinationType(TrajectoryEndType.DEAD_END);
        destination.setSourceType(TrajectoryEndType.DEAD_END);

        deadEnds.add(deadEnd);
    }

    private void transformAsDualWayRoad(Road myRoad){
        List<Position> positions = new ArrayList<>();
        int i;
        positions.add(myRoad.getA());
        for (i = 0; i < myRoad.size(); i++) {
            roadSections.remove(myRoad.get(i));
            positions.add(myRoad.get(i).getB());
        }

        roads.remove(myRoad.getId());
        roadEdges.get(myRoad.getA()).remove(myRoad.get(0));
        roadEdges.get(myRoad.getB()).remove(myRoad.get(myRoad.size()-1));

        for ( i = 1; i < positions.size(); i++) {
            addRoadSectionToRoad(positions.get(i-1),positions.get(i),myRoad.getId(),myRoad.getMaxSpeed(),false);
        }
    }

    /**
     * assemble the trajectories from L1 to L2
     */
    private void assembleLanes(Lane L1, Lane L2) {
        SimpleTrajectory source = L1.getInsertTrajectory();
        SimpleTrajectory destination = L2.getInsertTrajectory();
        TrajectoryJunction junction = TrajectoryJunction.computeJunction(source, destination);

        source.addDestination(junction);
        destination.addSource(junction);

        source.setDestinationType(TrajectoryEndType.SIMPLE);
        destination.setSourceType(TrajectoryEndType.SIMPLE);
    }

    //INTEGRITY CHECKING
    public int checkIntegrity() {
        int lanesProblems = 0;
        int intersectionProblems = 0;
        int deadEndProblems = 0;
        int trajectoryProblems = 0;
        Map<Trajectory, Boolean> trajectoryToCheck = new HashMap<>();

        //check integrity for simple trajectories

        for (RoadSection r : roadSections) {

            if(r instanceof DualWayRoadSection){
                lanesProblems += checkLane(((DualWayRoadSection)r).getLaneAB());
                lanesProblems += checkLane(((DualWayRoadSection)r).getLaneBA());

                trajectoryToCheck.put(((DualWayRoadSection)r).getLaneAB().getInsertTrajectory(), false);
                trajectoryToCheck.put(((DualWayRoadSection)r).getLaneBA().getInsertTrajectory(), false);
            }else{
                lanesProblems += checkLane(((OneWayRoadSection)r).getLane());
                trajectoryToCheck.put(((OneWayRoadSection)r).getLane().getInsertTrajectory(),false);
            }


        }
        LOGGER.debug("integrity check result for RoadSections(" + roadSections.size() + "): " + lanesProblems);

        intersectionProblems = checkIntersections();
        LOGGER.debug("integrity check result for intersections(" + intersectionMap.size() + "): " + intersectionProblems);

        for (EndRoadTrajectory trajectory : deadEnds) {
            if (trajectory.getLength() < 0) {
                deadEndProblems++;
                LOGGER.error("DEAD_END length negative");
            }

            if (trajectory.getSource() == null) {
                deadEndProblems++;
                LOGGER.error("DEAD_END source is null");
            }

            if (trajectory.getDestination() == null) {
                deadEndProblems++;
                LOGGER.error("DEAD_END destination is null");
            }

            trajectoryToCheck.put(trajectory, false);
        }
        LOGGER.debug("integrity check result for DeadEnds(" + deadEnds.size() + "): " + deadEndProblems);

        if (intersectionProblems == 0 && lanesProblems == 0 && deadEndProblems == 0) {
            trajectoryProblems = checkTrajectoryAccess(trajectoryToCheck);
        }
        //FIXME
        trajectoryProblems = 0;

        return intersectionProblems + lanesProblems + deadEndProblems + trajectoryProblems;
    }

    private int checkLane(Lane lane) {
        int problem = 0;
        if (lane.getInsertTrajectory().getLength() <= 0 || Double.isNaN(lane.getInsertTrajectory().getLength())) {
            problem++;
            LOGGER.error("trajectory is negative or NaN");
        }

        if (lane.getInsertTrajectory().getDestinationType() == TrajectoryEndType.UNDEFINED) {
            problem++;
            LOGGER.error("trajectory destination type is undefined");
        }

        if (lane.getInsertTrajectory().getDestinationsTrajectories().isEmpty()) {
            problem++;
            LOGGER.error("trajectory have no destination");
        }

        for (TrajectoryJunction junction : lane.getInsertTrajectory().getSourcesTrajectories().values()) {
            if (junction == null) {
                problem++;
                LOGGER.error("trajectory have null destination");
            }
        }
        return problem;
    }

    private int checkTrajectoryAccess(Map<Trajectory, Boolean> trajectoryMap) {
        int notExploredTrajetories = 0;
        LOGGER.debug("Total trajectories: " + trajectoryMap.size());
        Trajectory start = (Trajectory) trajectoryMap.keySet().toArray()[0];
        try {
            start.explore(trajectoryMap);
        } catch (StackOverflowError e) { //FIXME: Desastreux pour les performances !!!
            return 1;
        }

        for (boolean explored : trajectoryMap.values()) {
            if (!explored) {
                notExploredTrajetories++;
            }
        }
        LOGGER.debug(" trajectories not reached: " + notExploredTrajetories);

        return notExploredTrajetories;
    }

    private int checkIntersections(){
        int res = 0;
        for (Intersection I :intersectionMap.values()){
            if(!I.isValid()){
                res ++ ;
            }
        }
        return res;
    }

    //GETTERS

    public Road getRoad(UUID id) {
        return roads.get(id);
    }

    public Intersection getIntersection(Position P) {
        return intersectionMap.get(P);
    }

    public void saveSates(HistoryManager historyManager, int timestamp) {
        for (Road road : roads.values()) {
            historyManager.addRoadMetric(road.getMetrics(timestamp));
        }
    }
}
