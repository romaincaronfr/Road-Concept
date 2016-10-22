package fr.enssat.lanniontech.core;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoadManager {
    private Map<Position, ArrayList<RoadSection>> RoadEdges;
    private ArrayList<RoadSection> roadSections;
    private Map<Integer, Road> roads;

    public RoadManager() {
        RoadEdges = new HashMap<>();
        roadSections = new ArrayList<>();
        roads = new HashMap<>();
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
                //todo handle intersection creation
            }
        } else {
            RoadEdges.put(P, new ArrayList<>());
            RoadEdges.get(P).add(Rs1);
        }
    }

    public Road addRoadSectionToRoad(Position A, Position B, int id) {
        Road R = roads.get(id);
        if (R == null) {
            R = new Road(id);
            R.addSection(addRoadSection(A, B, R));
            roads.put(id, R);
        } else {
            R.addSection(addRoadSection(A, B, R));
        }
        return R;
    }

    public Road getRoad(int id) {
        return roads.get(id);
    }

    private void fuseRoadsSection(RoadSection RS1, RoadSection RS2, Position P) {
        RS2.getLeftLane(P).setNextLane(RS1.getRightLane(P));
        RS1.getLeftLane(P).setNextLane(RS2.getRightLane(P));
    }

}
