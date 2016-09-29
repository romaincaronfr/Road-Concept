package fr.enssat.lanniontech;

import fr.enssat.lanniontech.positioning.Position;
import fr.enssat.lanniontech.roadElements.Road;
import fr.enssat.lanniontech.roadElements.RoadSection;
import fr.enssat.lanniontech.vehicleElements.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {

    private static Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main (String[] args){
        Position A = new Position(40,0);
        Position B = new Position(40,0.01);
        Position C = new Position(40.01,0.02);
        Position D = new Position(40.02,0.02);
        RoadCreator DDE = new RoadCreator();

        /**
        RoadSection RS = DDE.addRoadSection(A,B);
        DDE.addRoadSection(B,C);
        DDE.addRoadSection(C,D);
         **/

        Road R= DDE.addRoad(A,B,42);
        DDE.addRoadSectionToRoad(B,C,42);
        DDE.addRoadSectionToRoad(C,D,42);

        System.out.println(Position.length(A,B)+Position.length(B,C)+Position.length(C,D));

        RoadSection RS = R.get(0);
        Vehicle V1 = new Vehicle(1,RS.getLaneA(),0,1,40);
        V1.log();



        //Vehicle V2 = new Vehicle(2,SR.getLane(),10,4,90);
        /*
        for (int i = 0; i<20;i++){
            V1.updateAcceleration();
            //V2.updateAcceleration();
            V1.updatePos(0.1);
            //V2.updatePos(0.1);
            V1.log();
            //V2.log();
        }
        */
    }

    public static int foo() {
        LOG.debug("foo() returns 4");
        return 4;
    } //For junit test
}
