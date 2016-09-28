package fr.enssat.lanniontech;

public class SingleLaneRoadSection extends RoadSection {

    SingleLaneRoadSection(Position A, Position B){
        super(A,B);
    }

    public Lane getLaneB(){
        return super.getLaneA();
    }

}
