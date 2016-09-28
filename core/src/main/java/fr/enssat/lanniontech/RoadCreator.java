package fr.enssat.lanniontech;

import fr.enssat.lanniontech.positioning.Position;
import fr.enssat.lanniontech.roadElements.RoadSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RoadCreator {
    private Map<Position,RoadSection> emptyRoadEdges;
    private Map<Position,RoadSection> emptySingleRoadEdges;//for roads with only one lane empty
    private ArrayList<RoadSection> roadSections;

    RoadCreator(){
        emptyRoadEdges = new HashMap<Position, RoadSection>();
        roadSections = new ArrayList<RoadSection>();
    }

    public RoadSection addRoadSection(Position A, Position B){
        RoadSection RS1 = new RoadSection(A,B);
        RoadSection RS2 = null;
        roadSections.add(RS1);
        if(emptyRoadEdges.containsKey(A)){
            RS2=emptyRoadEdges.get(A);

            assembleRoadsSection(RS1,RS2,A);
            emptyRoadEdges.remove(A);
        }else{
            emptyRoadEdges.put(A,RS1);
        }

        if(emptyRoadEdges.containsKey(B)){
            emptyRoadEdges.get(B);

            assembleRoadsSection(RS1,RS2,B);
            emptyRoadEdges.remove(B);
        }else{
            emptyRoadEdges.put(B,RS1);
        }
        return RS1;
    }



    private void assembleRoadsSection(RoadSection RS1,RoadSection RS2,Position P){
        RS2.getLeftLane(P).setNextLane(RS1.getRigthLane(P));
        RS1.getLeftLane(P).setNextLane(RS2.getRigthLane(P));
    }

}
