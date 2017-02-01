package fr.enssat.lanniontech.core.generators;

import fr.enssat.lanniontech.core.roadElements.roads.Road;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class VehicleBuffer {
    List<Road> sources;
    List<Road> destinations;
    List<VehicleKernel> kernels;
    Random gen;

    public VehicleBuffer() {
        this(new ArrayList<Road>(), new ArrayList<Road>());
    }

    public VehicleBuffer(Road source, Road destination) {
        this(new ArrayList<Road>(), new ArrayList<Road>());
        sources.add(source);
        destinations.add(destination);
    }

    public VehicleBuffer(List<Road> sources, List<Road> destinations) {
        this.sources = sources;
        this.destinations = destinations;
        kernels = new LinkedList<>();
        gen = new Random();
    }

    public void addKernels(List<VehicleKernel> K) {
        kernels.addAll(K);
    }

    public void removeKernel() {
        kernels.remove(0);
    }

    public VehicleKernel getKernel() {
        return kernels.get(0);
    }

    public Road getSource() {
        return sources.get(gen.nextInt(sources.size()));
    }

    public Road getDestination() {
        return destinations.get(gen.nextInt(destinations.size()));
    }

    public boolean isEmpty() {
        return kernels.isEmpty();
    }

    public int size() {
        return kernels.size();
    }
}
