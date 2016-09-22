package fr.enssat.lanniontech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main (String[] args){
        Position A = new Position(40,0);
        Position B = new Position(40,0.01);
        SingleLaneRoad SR = new SingleLaneRoad(A,B);
        System.out.println(Position.length(A,B));
        Vehicle V1 = new Vehicle(1,SR.getLane(),0,4,10);
        Vehicle V2 = new Vehicle(2,SR.getLane(),10,4,1);
        for (int i = 0; i<20;i++){
            V1.updateSpeed();
            V2.updateSpeed();
            V1.updatePos(0.1);
            V2.updatePos(0.1);
            V1.log();
            V2.log();
        }
    }

    public static int foo() {
        LOG.debug("foo() returns 4");
        return 4;
    } //For junit test
}
