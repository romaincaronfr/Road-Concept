import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.Tools;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;

import java.util.UUID;

public class TestSimulator {
    public static void main(String[] args) {
        //simulation init
        Simulator Sim = new Simulator();

        int vehicleNumber = 1;

        Position C = Sim.positionManager.addPosition(40, 0);
        Position D = Sim.positionManager.addPosition(40.1, 0);
        Position E = Sim.positionManager.addPosition(40.2, 0);
        Position B = Sim.positionManager.addPosition(40, 0.1);
        Position A = Sim.positionManager.addPosition(40.1, 0.1);
        Position F = Sim.positionManager.addPosition(40.2, 0.1);

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        UUID id3 = UUID.randomUUID();

        Road R = Sim.roadManager.addRoadSectionToRoad(A, B, id1);
        Sim.roadManager.addRoadSectionToRoad(B, C, id1);
        Sim.roadManager.addRoadSectionToRoad(C, D, id1);
        Sim.roadManager.addRoadSectionToRoad(D, A, id2);
        Sim.roadManager.addRoadSectionToRoad(D, E, id3);
        Sim.roadManager.addRoadSectionToRoad(E, F, id3);
        Sim.roadManager.addRoadSectionToRoad(F, A, id3);

        Sim.roadManager.closeRoads();

        int integrity = Sim.roadManager.checkIntegrity();
        if(integrity > 0){
            System.err.println(integrity + " roads corrupted !!!");
            return;
        }

        Sim.vehicleManager.addToSpawnArea(R);

        for (int i = 0; i < vehicleNumber; i++) {
            Sim.vehicleManager.addVehicle();
        }
        System.out.println(Sim.vehicleManager.getVehiclesNumber());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //run simulation

        Sim.launchSimulation(6000, 0.1,10);

        while (Sim.getProgress() < 1) {
            System.out.println("Sim time: " + Sim.getDuration());
            System.out.println("Sim progress: " + Tools.round(100 * Sim.getProgress(), 3));
            System.out.println("------------------------------------------------");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Sim.waitForEnd();

        System.out.println(Sim.getDuration());
    }
}
