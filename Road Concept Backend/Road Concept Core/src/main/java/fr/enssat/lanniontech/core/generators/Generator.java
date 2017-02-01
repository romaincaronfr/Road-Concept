package fr.enssat.lanniontech.core.generators;

import java.util.List;

public abstract class Generator {
    public abstract List<VehicleKernel> addVehicles(long timestamp);
}
