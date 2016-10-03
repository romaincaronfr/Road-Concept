package fr.enssat.lanniontech.core.roadElements;

import fr.enssat.lanniontech.core.positioning.Position;

//todo reverse inheritance

public class SingleLaneRoadSection extends RoadSection {

    SingleLaneRoadSection(Position A, Position B){
        super(A,B);
    }

    public Lane getLaneB(){
        return super.getLaneA();
    }

}
