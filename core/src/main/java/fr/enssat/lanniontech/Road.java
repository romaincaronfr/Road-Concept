package fr.enssat.lanniontech;

public class Road {
    protected Position A;
    protected Position B;
    protected double length;

    private Lane laneA; //this lane is on rigth side in A -> B scenario
    private Lane laneB; //this lane is on left side in A -> B scenario
    private PosFunction f;


    Road(Position A, Position B) {
        this.A = A;
        this.B = B;
        length = Position.length(A,B);
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
}
