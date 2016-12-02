package fr.enssat.lanniontech.core.generators;

import fr.enssat.lanniontech.core.vehicleElements.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class DiracGenerator extends Generator{

    private long startTimestamp;
    private int quantity;

    public DiracGenerator(int startTimestamp, int quantity){
        this.startTimestamp = startTimestamp;
        this.quantity = quantity;
    }

    @Override
    public List<VehicleKernel> addVehicles(long timestamp) {
        List<VehicleKernel> kernels = new ArrayList<>();
        if(timestamp == startTimestamp){
            for (int i = 0; i < quantity; i++) {
                kernels.add(new VehicleKernel(VehicleType.CAR));
            }
        }
        return kernels;
    }
}
