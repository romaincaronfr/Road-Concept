package fr.enssat.lanniontech.core.generators;

import fr.enssat.lanniontech.core.vehicleElements.VehicleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiracGenerator implements Generator {

    protected long startTimestamp;
    protected List<VehicleKernel> kernels;

    public DiracGenerator(int startTimestamp, int quantity, int carPercentage) {
        this.startTimestamp = startTimestamp;
        kernels = new ArrayList<>();

        int carCount = (int) Math.round(carPercentage / 100. * quantity);
        for (int i = 0; i < quantity; i++) {
            if (i >= carCount) {
                kernels.add(new VehicleKernel(VehicleType.TRUCK));
            } else {
                kernels.add(new VehicleKernel(VehicleType.CAR));
            }
        }
        Collections.shuffle(kernels); // Randomize apparition of cars and trucks
    }

    @Override
    public List<VehicleKernel> addVehicles(long timestamp) {
        List<VehicleKernel> res = new ArrayList<>();
        if (timestamp == startTimestamp) {
            res.addAll(kernels);
            kernels.clear();
        }
        return res;
    }
}
