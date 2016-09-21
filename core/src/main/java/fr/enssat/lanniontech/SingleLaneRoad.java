package fr.enssat.lanniontech;

public class SingleLaneRoad extends Road {
    private Lane lane;

    SingleLaneRoad(Position A,Position B){
        super(A,B);
        lane = new Lane(this.length);
    }

    public Lane getLane(){
        return lane;
    }

}
