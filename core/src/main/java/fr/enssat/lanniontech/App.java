package fr.enssat.lanniontech;

public class App {

    public static void main (String[] args){
        Position A = new Position(40,0);
        Position B = new Position(40,0.001);
        SingleLaneRoad SR = new SingleLaneRoad(A,B);
        Vehicle V = new Vehicle(SR.getLane().getEntryCell(),4.2,1);
        V.log();
        V.move(1);
        V.log();
    }

    public static int foo() {
        return 4;
    } //For junit test
}
