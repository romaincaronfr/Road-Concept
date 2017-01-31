package fr.enssat.lanniontech.core.generators;

import java.util.ArrayList;
import java.util.List;

public class UniformGenerator extends DiracGenerator {

    private double step;
    private double delta;

    public UniformGenerator(int startTimestamp, //in seconds
                            int quantity, int carPercentage, double length) {
        super(startTimestamp, quantity, carPercentage);
        step = quantity / length;
        delta = 0;
    }

    @Override
    public List<VehicleKernel> addVehicles(long timestamp) {
        List<VehicleKernel> res = new ArrayList<>();
        if (timestamp >= startTimestamp && !kernels.isEmpty()) {
            delta += step;
            int quantityToAdd = (int) delta;
            delta -= quantityToAdd;
            int i = 0;
            while (i < quantityToAdd && !kernels.isEmpty()) {
                res.add(kernels.get(0));
                kernels.remove(0);
                i++;
            }
        }
        return res;
    }
}
