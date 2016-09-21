package fr.enssat.lanniontech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by maelig on 19/09/2016.
 */
public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main (String[] args){
        Position A = new Position(40,0);
        Position B = new Position(40,0.001);
        SingleLaneRoad SR = new SingleLaneRoad(A,B);
        System.out.println(Position.length(A,B));
        Vehicle V = new Vehicle(1,SR.getLane(),81,4,1);
        V.log();
        for (int i = 0; i<10;i++){
            V.move(10);
            V.log();
        }
    }

    public static int foo() {
        LOG.debug("foo() returns 4");
        return 4;
    } //For junit test
}
