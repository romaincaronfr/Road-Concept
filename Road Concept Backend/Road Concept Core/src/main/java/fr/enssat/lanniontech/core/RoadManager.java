package fr.enssat.lanniontech.core;

import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;
import fr.enssat.lanniontech.core.roadElements.RoadSection;

import java.util.*;


public class RoadManager {
    private Map<Position,RoadSection> emptyRoadEdges;
    //private Map<Position,RoadSection> emptySingleRoadEdges;//for roads with only one lane empty
    private ArrayList<RoadSection> roadSections;
    private Map<Integer,Road> roads;

    public RoadManager(){
        emptyRoadEdges = new HashMap<Position, RoadSection>();
        roadSections = new ArrayList<RoadSection>();
        roads = new HashMap<Integer, Road>();
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
            RS2=emptyRoadEdges.get(B);
            assembleRoadsSection(RS1,RS2,B);
            emptyRoadEdges.remove(B);
        }else{
            emptyRoadEdges.put(B,RS1);
        }
        System.out.println(emptyRoadEdges);
        return RS1;
    }

    public Road addRoad(Position A,Position B,int id){
        Road R = new Road(id);
        System.out.println(A);
        System.out.println(B);
        R.addSection(addRoadSection(A,B));
        roads.put(id,R);
        return R;
    }

    public Road addRoadSectionToRoad(Position A, Position B,int id){
        Road R = roads.get(id);
        if (R==null){
            R = addRoad(A,B,id);
        }else{
            R.addSection(addRoadSection(A,B));
        }
        return R;
    }

    public Road getRoad(int id){
        return roads.get(id);
    }

    private void assembleRoadsSection(RoadSection RS1,RoadSection RS2,Position P){
        RS2.getLeftLane(P).setNextLane(RS1.getRigthLane(P));
        RS1.getLeftLane(P).setNextLane(RS2.getRigthLane(P));
    }

}
