import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.Tools;
import fr.enssat.lanniontech.core.positioning.Position;
import fr.enssat.lanniontech.core.roadElements.Road;

import java.util.UUID;

public class TestSimulator {
    public static void main(String[] args) {
        //simulation init
        Simulator Sim = new Simulator();

        int vehicleNumber = 1000;

        Position C = Sim.getPositionManager().addPosition(40, 0);
        Position D = Sim.getPositionManager().addPosition(40.1, 0);
        Position E = Sim.getPositionManager().addPosition(40.2, 0);
        Position B = Sim.getPositionManager().addPosition(40, 0.1);
        Position A = Sim.getPositionManager().addPosition(40.1, 0.1);
        Position F = Sim.getPositionManager().addPosition(40.2, 0.1);

        Position G0 = Sim.getPositionManager().addPosition(40.1, 0.2);
        Position G1 = Sim.getPositionManager().addPosition(40, 0.3);
        Position G2 = Sim.getPositionManager().addPosition(40.2, 0.3);

        UUID id1 = UUID.fromString("0-0-0-0-1");
        UUID id2 = UUID.fromString("0-0-0-0-2");
        UUID id3 = UUID.fromString("0-0-0-0-3");
        UUID id4 = UUID.fromString("0-0-0-0-4");
        UUID id5 = UUID.fromString("0-0-0-0-5");
        UUID id6 = UUID.fromString("0-0-0-0-6");

        Road R = Sim.getRoadManager().addRoadSectionToRoad(A, B, id1, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(B, C, id1, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(C, D, id1, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(D, A, id2, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(D, E, id3, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(E, F, id3, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(F, A, id3, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(A, G0, id4, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(G0, G1, id5, 50, false);
        Sim.getRoadManager().addRoadSectionToRoad(G0, G2, id6, 50, false);

        Sim.getRoadManager().closeRoads();

        int integrity = Sim.getRoadManager().checkIntegrity();

        if (integrity > 0) {
            System.err.println(integrity + " roads corrupted !!!");
            return;
        }

        Sim.getVehicleManager().setLivingArea(id1);
        Sim.getVehicleManager().setWorkingArea(id6);
        Sim.getVehicleManager().createTrafficGenerator(3600,36000,vehicleNumber,70);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //run simulation

        Sim.launchSimulation(24 * 3600, 0.1, 10);

        while (Sim.getProgress() < 1) {
            System.out.println("Sim time: " + Sim.getDuration());
            System.out.println("Sim progress: " + Tools.round(100 * Sim.getProgress(), 3));
            System.out.println("Active vehicles: " + Sim.getVehicleManager().getVehiclesNumber());
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
