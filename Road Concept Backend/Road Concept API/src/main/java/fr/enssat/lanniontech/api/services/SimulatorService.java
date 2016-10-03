package fr.enssat.lanniontech.api.services;

import fr.enssat.lanniontech.core.Simulator;

public class SimulatorService {

    private Simulator simulator = new Simulator(); // TODO: Store a Simulator instance in the session on login ?

    public boolean simulate() {
        boolean result = simulator.launchSimulation(24*3600,0.1);
        return result;
    }

    public double getStatus(){
        return this.simulator.getProgress();
    }
}
