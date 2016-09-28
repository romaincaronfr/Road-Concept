package fr.enssat.lanniontech;

public class RoadSection {
    protected Position A;
    protected Position B;
    protected double length;

    private Lane laneA; //this lane is on rigth side in A -> B scenario
    private Lane laneB; //this lane is on left side in A -> B scenario
    private PosFunction f;


    RoadSection(Position A, Position B) {
        this.A = A;
        this.B = B;
        length = Position.length(A,B);
        laneA = new Lane(this,length,null);
        laneB = new Lane(this,length,null);
        f = new PosFunction(A,B,length);
    }

    public Position getPosition(Lane myLane,double pos){
        if(myLane == laneA){
            return f.get(pos);
        }else{
            return f.get(length-pos);
        }
    }

    /**
     *
     * @param myLane
     * @param pos
     * @param widthPos position of the vehicle from the left side of the road
     * @return
     */
    public Position getPosition(Lane myLane,double pos,double widthPos){
        if(myLane == laneA){
            return f.get(pos,widthPos);
        }else{
            return f.get(length-pos,-widthPos);
        }
    }

    public Lane getRigthLane(Position P){
        if(P==A){
            return laneA;
        }else{
            return laneB;
        }
    }

    public Lane getLeftLane(Position P){
        if(P==B){
            return laneB;
        }else{
            return laneA;
        }
    }

    public Lane getLaneA(){
        return laneA;
    }

    public Lane getLaneB() {
        return laneB;
    }
}
