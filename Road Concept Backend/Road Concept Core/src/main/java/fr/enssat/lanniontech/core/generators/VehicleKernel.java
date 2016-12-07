package fr.enssat.lanniontech.core.generators;

import fr.enssat.lanniontech.core.vehicleElements.VehicleType;

public class VehicleKernel {

    private final VehicleType type;

    public VehicleKernel(VehicleType type) {
        this.type = type;
    }

    public VehicleType getType() {
        return type;
    }
}
