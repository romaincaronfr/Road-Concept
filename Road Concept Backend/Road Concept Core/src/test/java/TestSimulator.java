import fr.enssat.lanniontech.core.Simulator;
import fr.enssat.lanniontech.core.Tools;

public class TestSimulator {
    public static void main(String[] args) {
        Simulator Sim = new Simulator(10);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Sim.launchSimulation(24 * 3600, 0.1);

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
