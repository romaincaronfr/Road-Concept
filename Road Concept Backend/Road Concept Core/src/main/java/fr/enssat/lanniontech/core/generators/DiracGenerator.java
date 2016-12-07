package fr.enssat.lanniontech.core.generators;

import fr.enssat.lanniontech.core.vehicleElements.VehicleType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiracGenerator extends Generator {

    private long startTimestamp;
    private int quantity;
    private int carPercentage;

    public DiracGenerator(int startTimestamp, int quantity, int carPercentage) {
        this.startTimestamp = startTimestamp;
        this.quantity = quantity;
        this.carPercentage = carPercentage;
    }

    @Override
    public List<VehicleKernel> addVehicles(long timestamp) {
        List<VehicleKernel> kernels = new ArrayList<>();
        if (timestamp == startTimestamp) {
            int carCount = (int) Math.round(carPercentage / 100. * quantity);
            for (int i = 0; i < quantity; i++) {
                if (i >= carCount) {
                    kernels.add(new VehicleKernel(VehicleType.TRUCK));
                } else {
                    kernels.add(new VehicleKernel(VehicleType.CAR));
                }
            }
        }
        Collections.shuffle(kernels); // Randomize apparition of cars and trucks
        return kernels;
    }
}
